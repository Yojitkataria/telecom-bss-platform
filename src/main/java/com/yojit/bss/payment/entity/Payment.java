package com.yojit.bss.payment.entity;

import com.yojit.bss.billing.entity.Invoice;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, columnDefinition = "ENUM('UPI', 'CARD', 'NETBANKING')")
    private String method;

    @Column(nullable = false, columnDefinition = "ENUM('SUCCESS', 'FAILED', 'PENDING')")
    private String status;

    @Column(name = "txn_ref", nullable = false, unique = true, length = 50)
    private String txnRef;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;
}