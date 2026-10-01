package com.smartbiz.service.impl;

import com.smartbiz.dto.request.BusinessProfileRequest;
import com.smartbiz.dto.response.BusinessProfileResponse;
import com.smartbiz.entity.Business;
import com.smartbiz.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessProfileService {

    private final BusinessRepository businessRepository;

    private final BusinessContextService businessContextService;


    /**
     * Get currently logged-in business profile.
     */
    @Transactional(readOnly = true)
    public BusinessProfileResponse getProfile() {

        Business business =
                businessContextService.getCurrentBusiness();

        return BusinessProfileResponse.from(business);
    }


    /**
     * Update currently logged-in business profile.
     */
    @Transactional
    public BusinessProfileResponse updateProfile(
            BusinessProfileRequest request
    ) {

        Business business =
                businessContextService.getCurrentBusiness();


        // Business name
        business.setName(
                request.getName().trim()
        );


        // Phone
        business.setPhone(
                normalize(request.getPhone())
        );


        // Address
        business.setAddress(
                normalize(request.getAddress())
        );


        // Business type
        business.setBusinessType(
                normalize(request.getBusinessType())
        );


        // Registration number
        business.setRegistrationNumber(
                normalize(request.getRegistrationNumber())
        );


        // Website
        business.setWebsite(
                normalize(request.getWebsite())
        );


        // Logo URL
        business.setLogoUrl(
                normalize(request.getLogoUrl())
        );


        // Description
        business.setDescription(
                normalize(request.getDescription())
        );


        Business savedBusiness =
                businessRepository.save(business);


        return BusinessProfileResponse.from(
                savedBusiness
        );
    }


    /**
     * Convert empty strings to null.
     */
    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}