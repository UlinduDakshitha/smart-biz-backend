package com.smartbiz.dto.response;

import com.smartbiz.entity.Business;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessProfileResponse {

    private Integer businessId;

    private String name;

    private String email;

    private String phone;

    private String address;

    private String businessType;

    private String registrationNumber;

    private String website;

    private String logoUrl;

    private String description;

    public static BusinessProfileResponse from(Business business) {

        return BusinessProfileResponse.builder()
                .businessId(business.getBusinessId())
                .name(business.getName())
                .email(business.getEmail())
                .phone(business.getPhone())
                .address(business.getAddress())
                .businessType(business.getBusinessType())
                .registrationNumber(business.getRegistrationNumber())
                .website(business.getWebsite())
                .logoUrl(business.getLogoUrl())
                .description(business.getDescription())
                .build();
    }
}