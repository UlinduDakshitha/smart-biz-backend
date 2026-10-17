
package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.Payment;
import com.smartbiz.entity.Subscription;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.PaymentRepository;
import com.smartbiz.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(Integer subscriptionId) {

        Business business = getAuthenticatedBusiness();

        Subscription plan = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() ->
                        new RuntimeException("Subscription plan not found."));

        if (plan.getPrice() == null || plan.getPrice().signum() <= 0) {
            throw new RuntimeException(
                    "Please select a paid subscription plan.");
        }

        if (business.getSubscription() != null
                && business.getSubscription().getSubscriptionId()
                .equals(subscriptionId)) {
            throw new RuntimeException(
                    "You are already subscribed to this plan.");
        }

        boolean pendingPaymentExists =
                paymentRepository
                        .findByBusiness_BusinessIdOrderByCreatedAtDesc(
                                business.getBusinessId())
                        .stream()
                        .anyMatch(payment ->
                                payment.getPaymentStatus()
                                        == Payment.PaymentStatus.PENDING
                                        && payment.getApprovalStatus()
                                        == Payment.ApprovalStatus.PENDING);

        if (pendingPaymentExists) {
            throw new RuntimeException(
                    "You already have a pending payment.");
        }

        String merchantReference =
                "SB-" + UUID.randomUUID().toString().replace("-", "");

        Payment payment = Payment.builder()
                .business(business)
                .subscription(plan)
                .amount(plan.getPrice())
                .currency("LKR")
                .merchantReference(merchantReference)
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .approvalStatus(Payment.ApprovalStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
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
