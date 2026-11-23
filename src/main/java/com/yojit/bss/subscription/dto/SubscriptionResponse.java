package com.yojit.bss.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String planName;
    private String msisdn;
    private String iccid;
    private String status;
    private Integer billDay;
    private LocalDateTime activatedAt;
    private LocalDateTime createdAt;
}