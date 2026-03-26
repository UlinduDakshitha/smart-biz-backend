package com.smartbiz.controller;

import com.smartbiz.dto.request.LoginRequest;
import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.dto.response.AuthResponse;
import com.smartbiz.service.impl.AdminService;
import com.smartbiz.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;

    /**
     * Admin can also login via /api/auth/login (unified).
     * This endpoint kept for backward compatibility.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthResponse>> adminLogin(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.adminLogin(request);
        return ResponseEntity.ok(ApiResponse.success("Admin login successful", response));
    }

    @GetMapping("/businesses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<?>>> getAllBusinesses() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllBusinesses()));
    }

    @DeleteMapping("/businesses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBusiness(@PathVariable Integer id) {
        adminService.deleteBusiness(id);
        return ResponseEntity.ok(ApiResponse.success("Business deleted", null));
    }


    @GetMapping("/ai-usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<?>>> getAiUsage() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllAiUsage()));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getSystemStats()));
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<?>>> getSubscriptions() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllSubscriptions()));
    }

    @PostMapping("/subscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createSubscription(@RequestBody com.smartbiz.entity.Subscription subscription) {
        return ResponseEntity.ok(ApiResponse.success(adminService.createSubscription(subscription)));
    }

    @PutMapping("/subscriptions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateSubscription(@PathVariable Integer id, @RequestBody com.smartbiz.entity.Subscription subscription) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateSubscription(id, subscription)));
    }

    @DeleteMapping("/subscriptions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSubscription(@PathVariable Integer id) {
        adminService.deleteSubscription(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription deleted", null));
    }
}
