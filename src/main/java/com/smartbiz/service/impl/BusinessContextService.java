package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessContextService {

    private final BusinessRepository businessRepository;

    public Business getCurrentBusiness() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return businessRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Business not found"));
    }

    public Integer getCurrentBusinessId() {
        return getCurrentBusiness().getBusinessId();
    }
}
