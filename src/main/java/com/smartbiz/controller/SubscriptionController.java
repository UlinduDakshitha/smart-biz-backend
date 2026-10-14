
package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.dto.response.SubscriptionStatusResponse;
import com.smartbiz.service.impl.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<SubscriptionStatusResponse>> getStatus() {
        return ResponseEntity.ok(
                ApiResponse.success(subscriptionService.getCurrentStatus())
        );
    }
}
