package com.smartbiz.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusinessProfileRequest {

    @NotBlank(message = "Business name is required")
    @Size(max = 150, message = "Business name must be at most 150 characters")
    private String name;

    @Size(max = 30, message = "Phone number must be at most 30 characters")
    private String phone;

    @Size(max = 500, message = "Address must be at most 500 characters")
    private String address;

    @Size(max = 100, message = "Business type must be at most 100 characters")
    private String businessType;

    @Size(max = 100, message = "Registration number must be at most 100 characters")
    private String registrationNumber;

    @Size(max = 255, message = "Website must be at most 255 characters")
    private String website;

    @Size(max = 500, message = "Logo URL must be at most 500 characters")
    private String logoUrl;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    private String description;
}