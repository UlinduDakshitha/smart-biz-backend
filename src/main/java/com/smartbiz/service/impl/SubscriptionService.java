
package com.smartbiz.service.impl;

import com.smartbiz.dto.response.SubscriptionStatusResponse;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Subscription;
import com.smartbiz.entity.SubscriptionRequest;
import com.smartbiz.entity.SubscriptionRequest.RequestStatus;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.SubscriptionRepository;
import com.smartbiz.repository.SubscriptionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;

    public SubscriptionStatusResponse getCurrentStatus() {

        Business business = getAuthenticatedBusiness();

        LocalDate startDate = business.getTrialStartDate();
        LocalDate endDate = business.getTrialEndDate();

        long daysRemaining = 0;
        String status = "NOT_STARTED";

        if (endDate != null) {
            LocalDate today = LocalDate.now();

            if (!today.isBefore(endDate)) {
                status = "EXPIRED";
            } else {
                daysRemaining = ChronoUnit.DAYS.between(today, endDate);
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

    @Transactional
    public SubscriptionRequest requestUpgrade(Integer subscriptionId) {

        Business business = getAuthenticatedBusiness();

        Subscription requestedSubscription = subscriptionRepository
                .findById(subscriptionId)
                .orElseThrow(() ->
                        new RuntimeException("Subscription plan not found."));

        if (requestedSubscription.getPrice() == null
                || requestedSubscription.getPrice().signum() <= 0) {
            throw new RuntimeException(
                    "Please select a paid subscription plan.");
        }

        if (business.getSubscription() != null
                && business.getSubscription().getSubscriptionId()
                .equals(subscriptionId)) {
            throw new RuntimeException(
                    "You are already subscribed to this plan.");
        }

        boolean pendingRequestExists =
                subscriptionRequestRepository
                        .existsByBusiness_BusinessIdAndStatus(
                                business.getBusinessId(),
                                RequestStatus.PENDING
                        );

        if (pendingRequestExists) {
            throw new RuntimeException(
                    "You already have a pending subscription request.");
        }

        SubscriptionRequest request = SubscriptionRequest.builder()
                .business(business)
                .requestedSubscription(requestedSubscription)
                .status(RequestStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        return subscriptionRequestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionRequest> getMyUpgradeRequests() {

        Business business = getAuthenticatedBusiness();

        return subscriptionRequestRepository
                .findByBusiness_BusinessIdOrderByRequestDateDesc(
                        business.getBusinessId()
                );
    }

    private Business getAuthenticatedBusiness() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Authentication required.");
        }

        String email = authentication.getName();

        return businessRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Business not found."));
    }
}
