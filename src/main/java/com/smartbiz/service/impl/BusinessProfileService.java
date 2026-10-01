package com.smartbiz.service.impl;

import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessProfileService {

    private final BusinessRepository businessRepository;
    private final BusinessContextService businessContextService;

    public Business getProfile() {

        return businessContextService.getCurrentBusiness();
    }

    public Business updateProfile(
            String name,
            String phone,
            String address,
            String businessType,
            String registrationNumber,
            String website,
            String logoUrl,
            String description
    ) {

        Business business =
                businessContextService.getCurrentBusiness();

        business.setName(name);
        business.setPhone(phone);
        business.setAddress(address);
        business.setBusinessType(businessType);
        business.setRegistrationNumber(registrationNumber);
        business.setWebsite(website);
        business.setLogoUrl(logoUrl);
        business.setDescription(description);

        return businessRepository.save(business);
    }
}