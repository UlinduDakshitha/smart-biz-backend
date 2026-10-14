
package com.smartbiz.service.impl;

import com.smartbiz.dto.response.SubscriptionStatusResponse;
import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final BusinessRepository businessRepository;

    public SubscriptionStatusResponse getCurrentStatus() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Authentication required.");
        }

        String email = authentication.getName();

        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found."));

        LocalDate startDate = business.getTrialStartDate();
        LocalDate endDate = business.getTrialEndDate();

        long daysRemaining = 0;
        String status = "NOT_STARTED";

        if (endDate != null) {
            LocalDate today = LocalDate.now();

            if (endDate.isBefore(today)) {
                status = "EXPIRED";
            } else {
                daysRemaining = java.time.temporal.ChronoUnit.DAYS
                        .between(today, endDate);
                status = "ACTIVE";
            }
        }

        return SubscriptionStatusResponse.builder()
                .planName(
                        business.getSubscription() != null
                                ? business.getSubscription().getPlanName()
                                : "No Plan"
                )
                .status(status)
                .trialStartDate(startDate)
                .trialEndDate(endDate)
                .daysRemaining(daysRemaining)
                .build();
    }
}
