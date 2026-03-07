package com.smartbiz.service.impl;

import com.smartbiz.dto.request.*;
import com.smartbiz.dto.response.AuthResponse;
import com.smartbiz.entity.Admin;
import com.smartbiz.entity.Business;
import com.smartbiz.entity.Subscription;
import com.smartbiz.repository.AdminRepository;
import com.smartbiz.repository.BusinessRepository;
import com.smartbiz.repository.SubscriptionRepository;
import com.smartbiz.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;
    private final EmailService emailService;

    /**
     * UNIFIED LOGIN — checks admin first, then business.
     * Single endpoint for everyone. Role in response tells frontend where to redirect.
     */
    public AuthResponse unifiedLogin(LoginRequest request) {
        // 1. Try Admin
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }
            String token = jwtUtils.generateToken(admin.getEmail(), "ADMIN");
            return AuthResponse.builder()
                    .token(token).email(admin.getEmail())
                    .name(admin.getName()).role("ADMIN").id(admin.getAdminId()).build();
        }

        // 2. Try Business
        Optional<Business> businessOpt = businessRepository.findByEmail(request.getEmail());
        if (businessOpt.isPresent()) {
            Business business = businessOpt.get();
            if (!passwordEncoder.matches(request.getPassword(), business.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }
            String token = jwtUtils.generateToken(business.getEmail(), "BUSINESS");
            return AuthResponse.builder()
                    .token(token).email(business.getEmail())
                    .name(business.getName()).role("BUSINESS").id(business.getBusinessId()).build();
        }

        throw new RuntimeException("Invalid email or password");
    }

    public AuthResponse adminLogin(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword()))
            throw new RuntimeException("Invalid credentials");
        String token = jwtUtils.generateToken(admin.getEmail(), "ADMIN");
        return AuthResponse.builder()
                .token(token).email(admin.getEmail())
                .name(admin.getName()).role("ADMIN").id(admin.getAdminId()).build();
    }

    public AuthResponse businessLogin(LoginRequest request) {
        Business business = businessRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), business.getPassword()))
            throw new RuntimeException("Invalid credentials");
        String token = jwtUtils.generateToken(business.getEmail(), "BUSINESS");
        return AuthResponse.builder()
                .token(token).email(business.getEmail())
                .name(business.getName()).role("BUSINESS").id(business.getBusinessId()).build();
    }

    public AuthResponse registerBusiness(BusinessRegisterRequest request) {
        if (businessRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered");

        Business.BusinessBuilder builder = Business.builder()
                .name(request.getName()).email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone()).address(request.getAddress());

        if (request.getSubscriptionId() != null)
            subscriptionRepository.findById(request.getSubscriptionId()).ifPresent(builder::subscription);

        Business saved = businessRepository.save(builder.build());
        String token = jwtUtils.generateToken(saved.getEmail(), "BUSINESS");
        return AuthResponse.builder()
                .token(token).email(saved.getEmail())
                .name(saved.getName()).role("BUSINESS").id(saved.getBusinessId()).build();
    }

    // ── Forgot Password Flow ────────────────────────────────────────────────

    /** Step 1 — Send OTP to email */
    public void sendForgotPasswordOtp(ForgotPasswordRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        String name = resolveAccountName(email);

        // Security: don't reveal if email exists; but if it does, send OTP
        if (name != null) {
            String otp = otpService.generateAndStoreOtp(email);
            emailService.sendOtpEmail(email, name, otp);
        }
        // Always return success to prevent email enumeration
    }

    /** Step 2 — Verify OTP (returns true/false, used before showing reset form) */
    public void verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        if (!otpService.verifyOtp(email, request.getOtp()))
            throw new RuntimeException("Invalid or expired OTP. Please try again.");
    }

    /** Step 3 — Reset password after OTP verified */
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        if (!otpService.verifyOtp(email, request.getOtp()))
            throw new RuntimeException("Invalid or expired OTP.");

        String hashed = passwordEncoder.encode(request.getNewPassword());

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            admin.get().setPassword(hashed);
            adminRepository.save(admin.get());
        } else {
            Business business = businessRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Account not found."));
            business.setPassword(hashed);
            businessRepository.save(business);
        }

        otpService.clearOtp(email);
    }

    private String resolveAccountName(String email) {
        return adminRepository.findByEmail(email).map(Admin::getName)
                .or(() -> businessRepository.findByEmail(email).map(Business::getName))
                .orElse(null);
    }
}
