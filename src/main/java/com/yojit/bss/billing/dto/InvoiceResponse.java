package com.yojit.bss.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNo;
    private Long subscriptionId;
    private Long customerId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal planCharge;
    private BigDecimal addonCharge;
    private BigDecimal overageCharge;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate issuedOn;
    private LocalDate dueDate;
    private List<InvoiceItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemDto {
        private String itemType;
        private String description;
        private BigDecimal amount;
    }
}