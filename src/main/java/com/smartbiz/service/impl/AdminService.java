
package com.smartbiz.service.impl;

import com.smartbiz.entity.*;
import com.smartbiz.entity.SubscriptionRequest.RequestStatus;
import com.smartbiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartbiz.repository.PaymentRepository;
import com.smartbiz.entity.Payment;
import com.smartbiz.entity.Business;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BusinessRepository businessRepository;
    private final AiUsageRepository aiUsageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final PaymentRepository paymentRepository;

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    public Business getBusinessById(Integer id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    public void deleteBusiness(Integer id) {
        businessRepository.deleteById(id);
    }

    public List<AiUsage> getAllAiUsage() {
        return aiUsageRepository.findAll();
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubscription(Integer id, Subscription updated) {
        Subscription existing = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        existing.setPlanName(updated.getPlanName());
        existing.setPrice(updated.getPrice());
        existing.setDurationDays(updated.getDurationDays());

        return subscriptionRepository.save(existing);
    }

    public void deleteSubscription(Integer id) {
        subscriptionRepository.deleteById(id);
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBusinesses", businessRepository.count());
        stats.put("totalAiRequests", aiUsageRepository.count());
        stats.put("totalInvoices", invoiceRepository.count());
        stats.put("totalSubscriptions", subscriptionRepository.count());
        return stats;
    }

    // Get all pending subscription upgrade requests
    @Transactional(readOnly = true)
    public List<SubscriptionRequest> getPendingSubscriptionRequests() {
        return subscriptionRequestRepository
                .findByStatusOrderByRequestDateDesc(RequestStatus.PENDING);
    }

    // Approve a subscription upgrade request
    @Transactional
    public SubscriptionRequest approveSubscriptionRequest(
            Integer requestId, String adminNote) {

        SubscriptionRequest request = subscriptionRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException("Subscription request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException(
                    "This subscription request has already been reviewed.");
        }

        Business business = request.getBusiness();
        Subscription requestedPlan = request.getRequestedSubscription();

        if (requestedPlan.getPrice() == null
                || requestedPlan.getPrice().signum() <= 0) {
            throw new RuntimeException(
                    "Cannot approve a request for a free plan.");
        }

        business.setSubscription(requestedPlan);
        business.setTrialStartDate(null);
        business.setTrialEndDate(null);

        businessRepository.save(business);

        request.setStatus(RequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setAdminNote(adminNote);

        return subscriptionRequestRepository.save(request);
    }

    // Reject a subscription upgrade request
    @Transactional
    public SubscriptionRequest rejectSubscriptionRequest(
            Integer requestId, String adminNote) {

        SubscriptionRequest request = subscriptionRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new RuntimeException("Subscription request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException(
                    "This subscription request has already been reviewed.");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setAdminNote(adminNote);

        return subscriptionRequestRepository.save(request);
    }


    // Get all payments waiting for admin approval
    @Transactional(readOnly = true)
    public List<Payment> getPendingPayments() {
        return paymentRepository
                .findByApprovalStatusOrderByCreatedAtDesc(
                        Payment.ApprovalStatus.PENDING
                );
    }

    // Approve a payment only after gateway verification
    @Transactional
    public Payment approvePayment(Integer paymentId, String adminNote) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found."));

        if (payment.getPaymentStatus() != Payment.PaymentStatus.VERIFIED) {
            throw new RuntimeException(
                    "Cannot approve payment: payment has not been verified."
            );
        }

        if (payment.getApprovalStatus() != Payment.ApprovalStatus.PENDING) {
            throw new RuntimeException(
                    "This payment has already been reviewed."
            );
        }

        Business business = payment.getBusiness();
        Subscription plan = payment.getSubscription();

        business.setSubscription(plan);
        business.setTrialStartDate(null);
        business.setTrialEndDate(null);

        businessRepository.save(business);

        payment.setApprovalStatus(Payment.ApprovalStatus.APPROVED);
        payment.setReviewedAt(LocalDateTime.now());
        payment.setAdminNote(adminNote);

        return paymentRepository.save(payment);
    }

    // Reject a payment
    @Transactional
    public Payment rejectPayment(Integer paymentId, String adminNote) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found."));

        if (payment.getApprovalStatus() != Payment.ApprovalStatus.PENDING) {
            throw new RuntimeException(
                    "This payment has already been reviewed."
            );
        }

        payment.setApprovalStatus(Payment.ApprovalStatus.REJECTED);
        payment.setReviewedAt(LocalDateTime.now());
        payment.setAdminNote(adminNote);

        return paymentRepository.save(payment);
    }

}
