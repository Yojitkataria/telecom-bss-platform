package com.yojit.bss.usage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageSummaryResponse {
    private Long subscriptionId;
    private String planName;
    
    // Data (MB)
    private BigDecimal dataUsedMb;
    private Integer dataAllowedMb;
    private BigDecimal dataOverageMb;

    // Voice (Minutes)
    private BigDecimal voiceUsedMinutes;
    private Integer voiceAllowedMinutes;
    private BigDecimal voiceOverageMinutes;

    // SMS
    private BigDecimal smsUsed;
    private Integer smsAllowed;
    private BigDecimal smsOverage;
}