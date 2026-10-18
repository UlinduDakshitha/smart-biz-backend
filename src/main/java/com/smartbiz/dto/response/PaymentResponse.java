package com.smartbiz.dto;

import com.smartbiz.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Integer paymentId,
        Integer businessId,
        String businessName,
        String businessEmail,
        Integer subscriptionId,
        String planName,
        BigDecimal amount,
        String currency,
        String merchantReference,
        String paymentStatus,
        String approvalStatus,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime reviewedAt,
        String adminNote
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getBusiness().getBusinessId(),
                payment.getBusiness().getName(),
                payment.getBusiness().getEmail(),
                payment.getSubscription().getSubscriptionId(),
                payment.getSubscription().getPlanName(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMerchantReference(),
                payment.getPaymentStatus().name(),
                payment.getApprovalStatus().name(),
                payment.getCreatedAt(),
                payment.getPaidAt(),
                payment.getReviewedAt(),
                payment.getAdminNote()
        );
    }
}