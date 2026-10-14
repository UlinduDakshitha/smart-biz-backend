
package com.smartbiz.security;

import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SubscriptionAccessFilter extends OncePerRequestFilter {

    private final BusinessRepository businessRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/api/auth/")
                || path.startsWith("/api/admin/")
                || path.equals("/api/subscription/status")
                || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_BUSINESS"))) {

            filterChain.doFilter(request, response);
            return;
        }

        String email = authentication.getName();

        Business business = businessRepository.findByEmail(email)
                .orElse(null);

        if (business == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Business account not found."
            );
            return;
        }

        boolean isFreePlan = business.getSubscription() != null
                && "Free".equalsIgnoreCase(
                business.getSubscription().getPlanName()
        );

        if (isFreePlan) {
            LocalDate trialEndDate = business.getTrialEndDate();

            boolean expired = trialEndDate == null
                    || !LocalDate.now().isBefore(trialEndDate);

            if (expired) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                response.getWriter().write(
                        "{\"success\":false,"
                                + "\"message\":\"Your free trial has expired. Please upgrade your subscription.\","
                                + "\"data\":null}"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
