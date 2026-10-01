package com.smartbiz.controller;

import com.smartbiz.dto.request.BusinessProfileRequest;
import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.dto.response.BusinessProfileResponse;
import com.smartbiz.service.impl.BusinessProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/business/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUSINESS')")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;


    /**
     * Get currently logged-in business profile.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponse>>
    getProfile() {

        BusinessProfileResponse response =
                businessProfileService.getProfile();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business profile loaded successfully",
                        response
                )
        );
    }


    /**
     * Update currently logged-in business profile.
     */
    @PutMapping
    public ResponseEntity<ApiResponse<BusinessProfileResponse>>
    updateProfile(
            @Valid
            @RequestBody
            BusinessProfileRequest request
    ) {

        BusinessProfileResponse response =
                businessProfileService.updateProfile(
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business profile updated successfully",
                        response
                )
        );
    }
}