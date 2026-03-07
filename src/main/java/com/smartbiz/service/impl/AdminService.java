package com.smartbiz.service.impl;

import com.smartbiz.entity.*;
import com.smartbiz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BusinessRepository businessRepository;
    private final AiUsageRepository aiUsageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceRepository invoiceRepository;

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
}
