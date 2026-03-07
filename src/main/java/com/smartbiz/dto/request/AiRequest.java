package com.smartbiz.dto.request;

import lombok.Data;

@Data
public class AiRequest {
    private String feature;
    private String prompt;
}
