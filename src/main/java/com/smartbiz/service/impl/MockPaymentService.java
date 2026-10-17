
        package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.entity.Payment;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class MockPaymentService {

    private final PaymentRepository paymentRepository;
    private final BusinessRepository businessRepository;

    @Value("${app.payment.mock-enabled:false}")
    private boolean mockEnabled;

    @Transactional
    public Payment simulatePayment(Integer paymentId, boolean success) {

        if (!mockEnabled) {
            throw new IllegalStateException(
                    "Mock payments are disabled."
            );
        }

        // Get the logged-in user's email
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null) {
            throw new IllegalStateException("User is not authenticated.");
        }

        String email = authentication.getName();

        // Find the logged-in business
        Business business = businessRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Business not found."));

        // Find the payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found."));

        // Prevent users from changing another business's payment
        if (!payment.getBusiness().getBusinessId()
                .equals(business.getBusinessId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You cannot access this payment."
            );
        }

        // Prevent processing the same payment twice
        if (payment.getPaymentStatus() != Payment.PaymentStatus.PENDING
                || payment.getApprovalStatus()
                != Payment.ApprovalStatus.PENDING) {
            throw new IllegalStateException(
                    "This payment cannot be processed again."
            );
        }

        if (success) {
            payment.setPaymentStatus(Payment.PaymentStatus.VERIFIED);
            payment.setPaidAt(LocalDateTime.now());
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        }

        return paymentRepository.save(payment);
    }
}
