
package com.smartbiz.controller;

import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.dto.response.SubscriptionStatusResponse;
import com.smartbiz.entity.SubscriptionRequest;
import com.smartbiz.service.impl.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/upgrade-request/{subscriptionId}")
    public ResponseEntity<ApiResponse<SubscriptionRequest>> requestUpgrade(
            @PathVariable Integer subscriptionId) {

        SubscriptionRequest request =
                subscriptionService.requestUpgrade(subscriptionId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Subscription upgrade request submitted successfully.",
                        request
                )
        );
    }

    @GetMapping("/upgrade-requests")
    public ResponseEntity<ApiResponse<List<SubscriptionRequest>>> getMyUpgradeRequests() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        subscriptionService.getMyUpgradeRequests()
                )
        );
    }
}
