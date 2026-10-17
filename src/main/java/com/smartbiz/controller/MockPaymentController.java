
        package com.smartbiz.controller;

import com.smartbiz.entity.Payment;
import com.smartbiz.service.impl.MockPaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock-payments")
@Profile("dev")
@RequiredArgsConstructor
public class MockPaymentController {

    private final MockPaymentService mockPaymentService;

    @PostMapping("/{paymentId}/success")
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<Payment> simulateSuccess(
            @PathVariable Integer paymentId) {

        return ResponseEntity.ok(
                mockPaymentService.simulatePayment(paymentId, true)
        );
    }

    @PostMapping("/{paymentId}/failure")
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<Payment> simulateFailure(
            @PathVariable Integer paymentId) {

        return ResponseEntity.ok(
                mockPaymentService.simulatePayment(paymentId, false)
        );
    }
}
