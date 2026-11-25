package com.yojit.bss.billing.entity;

import com.yojit.bss.customer.entity.Customer;
import com.yojit.bss.subscription.entity.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", nullable = false, unique = true, length = 30)
    private String invoiceNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "plan_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal planCharge;

    @Column(name = "addon_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal addonCharge;

    @Column(name = "overage_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal overageCharge;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, columnDefinition = "ENUM('GENERATED', 'PAID', 'OVERDUE', 'CANCELLED')")
    private String status;

    @Column(name = "issued_on", nullable = false)
    private LocalDate issuedOn;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
}