
package com.smartbiz.service.impl;

import com.smartbiz.entity.*;
import com.smartbiz.entity.SubscriptionRequest.RequestStatus;
import com.smartbiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
