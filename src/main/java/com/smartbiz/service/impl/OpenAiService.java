package com.smartbiz.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbiz.entity.AiUsage;
import com.smartbiz.entity.Business;
import com.smartbiz.repository.AiUsageRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final AiUsageRepository aiUsageRepository;
    private final BusinessContextService businessContextService;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String feature, String userPrompt) {
        Business business = businessContextService.getCurrentBusiness();

        String systemPrompt = getSystemPrompt(feature);
        String fullPrompt = buildPrompt(feature, userPrompt, business.getName());

        String aiResponse;
        try {
            aiResponse = callOpenAi(systemPrompt, fullPrompt);
        } catch (Exception e) {
            aiResponse = "AI service is currently unavailable. Please check your API key configuration. Error: " + e.getMessage();
        }

        // Log AI usage
        AiUsage usage = AiUsage.builder()
                .business(business)
                .feature(feature)
                .prompt(userPrompt)
                .response(aiResponse)
                .date(LocalDateTime.now())
                .build();
        aiUsageRepository.save(usage);

        return aiResponse;
    }

    private String callOpenAi(String systemPrompt, String userPrompt) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new java.util.HashMap<>() {{
            put("model", model);
            put("max_tokens", 1000);
            put("messages", new java.util.ArrayList<>() {{
                add(new java.util.HashMap<>() {{
                    put("role", "system");
                    put("content", systemPrompt);
                }});
                add(new java.util.HashMap<>() {{
                    put("role", "user");
                    put("content", userPrompt);
                }});
            }});
        }});

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("OpenAI API error: " + response.code());
            }
            String responseBody = response.body().string();
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }

    private String getSystemPrompt(String feature) {
        return switch (feature) {
            case "INSIGHTS" -> "You are a business analyst AI. Analyze business data and provide clear, actionable insights. Be concise and practical.";
            case "EMAIL" -> "You are a professional email writer. Write clear, professional, and courteous business emails based on the given context.";
            case "INVOICE_SUMMARY" -> "You are a financial assistant. Explain invoices in simple, easy-to-understand language for business owners.";
            case "SOCIAL_MEDIA" -> "You are a social media marketing expert. Create engaging, platform-appropriate posts that drive customer engagement.";
            default -> "You are a helpful business assistant for SmartBiz platform.";
        };
    }

    private String buildPrompt(String feature, String userPrompt, String businessName) {
        return "Business name: " + businessName + "\n\n" + userPrompt;
    }
}
