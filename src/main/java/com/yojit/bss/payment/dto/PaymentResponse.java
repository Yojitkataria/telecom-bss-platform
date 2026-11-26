package com.yojit.bss.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long invoiceId;
    private String invoiceNo;
    private BigDecimal amount;
    private String method;
    private String status;
    private String txnRef;
    private LocalDateTime paidAt;
}