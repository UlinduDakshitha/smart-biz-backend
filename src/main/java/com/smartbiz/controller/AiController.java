package com.smartbiz.controller;

import com.smartbiz.dto.request.AiRequest;
import com.smartbiz.dto.response.ApiResponse;
import com.smartbiz.service.impl.AiUsageService;
import com.smartbiz.service.impl.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final OpenAiService openAiService;
    private final AiUsageService aiUsageService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, String>>> generate(@RequestBody AiRequest request) {
        String response = openAiService.generateResponse(request.getFeature(), request.getPrompt());
        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        result.put("feature", request.getFeature());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<?>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.success(aiUsageService.getHistory()));
    }
}
