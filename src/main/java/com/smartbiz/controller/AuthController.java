package com.smartbiz.controller;

import com.smartbiz.dto.request.*;
import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.dto.response.AuthResponse;
import com.smartbiz.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * UNIFIED LOGIN — works for both Admin and Business.
     * Response contains "role": "ADMIN" or "BUSINESS" so frontend can redirect.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.unifiedLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody BusinessRegisterRequest request) {
        AuthResponse response = authService.registerBusiness(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    /**
     * Forgot Password — Step 1: Submit email → OTP sent
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.sendForgotPasswordOtp(request);
        return ResponseEntity.ok(ApiResponse.success(
            "If your email is registered, an OTP has been sent. Please check your inbox.", null));
    }

    /**
     * Forgot Password — Step 2: Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully.", null));
    }

    /**
     * Forgot Password — Step 3: Reset password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please login.", null));
    }
}
