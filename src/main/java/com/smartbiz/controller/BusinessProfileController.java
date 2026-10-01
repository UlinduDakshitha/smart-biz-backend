package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.entity.Business;
import com.smartbiz.service.impl.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business/profile")
@RequiredArgsConstructor
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    @GetMapping
    public ResponseEntity<ApiResponse<Business>> getProfile() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        businessProfileService.getProfile()
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Business>> updateProfile(
            @RequestBody Business business
    ) {

        Business updated =
                businessProfileService.updateProfile(
                        business.getName(),
                        business.getPhone(),
                        business.getAddress(),
                        business.getBusinessType(),
                        business.getRegistrationNumber(),
                        business.getWebsite(),
                        business.getLogoUrl(),
                        business.getDescription()
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business profile updated",
                        updated
                )
        );
    }
}