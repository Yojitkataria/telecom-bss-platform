package com.yojit.bss.catalog.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanRequest {

    @NotBlank(message = "Plan name is required")
    @Size(max = 80, message = "Plan name must not exceed 80 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @NotNull(message = "Monthly fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Monthly fee cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Monthly fee format is invalid (max 8 integer digits, 2 decimals)")
    private BigDecimal monthlyFee;

    @NotNull(message = "Data MB allocation is required")
    @Min(value = 0, message = "Data MB cannot be negative")
    private Integer dataMb;

    @NotNull(message = "Voice minutes limit is required")
    @Min(value = 0, message = "Voice minutes cannot be negative")
    private Integer voiceMinutes;

    @NotNull(message = "SMS count limit is required")
    @Min(value = 0, message = "SMS count cannot be negative")
    private Integer smsCount;

    @NotNull(message = "Overage data rate per MB is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Overage data rate cannot be negative")
    @Digits(integer = 4, fraction = 4, message = "Overage data rate format is invalid")
    private BigDecimal overageDataPerMb;

    @NotNull(message = "Overage voice rate per minute is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Overage voice rate cannot be negative")
    @Digits(integer = 4, fraction = 4, message = "Overage voice rate format is invalid")
    private BigDecimal overageVoicePerMin;

    @NotNull(message = "Overage SMS rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Overage SMS rate cannot be negative")
    @Digits(integer = 4, fraction = 4, message = "Overage SMS rate format is invalid")
    private BigDecimal overageSmsEach;

    @NotNull(message = "Active status must be specified")
    private Boolean active;
}