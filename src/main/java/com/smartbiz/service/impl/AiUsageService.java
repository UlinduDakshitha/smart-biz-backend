package com.smartbiz.service.impl;

import com.smartbiz.entity.AiUsage;
import com.smartbiz.repository.AiUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiUsageService {

    private final AiUsageRepository aiUsageRepository;
    private final BusinessContextService businessContextService;

    public List<AiUsage> getHistory() {
        return aiUsageRepository.findByBusiness_BusinessId(businessContextService.getCurrentBusinessId());
    }
}
