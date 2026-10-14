
package com.smartbiz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatusResponse {

    private String planName;
    private String status;
    private LocalDate trialStartDate;
    private LocalDate trialEndDate;
    private long daysRemaining;
}
