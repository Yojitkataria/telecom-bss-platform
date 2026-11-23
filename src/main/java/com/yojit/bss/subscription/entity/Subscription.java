package com.yojit.bss.subscription.entity;

import com.yojit.bss.catalog.entity.Plan;
import com.yojit.bss.customer.entity.Customer;
import com.yojit.bss.sim.entity.SimCard;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sim_id", nullable = false)
    private SimCard simCard;

    @Column(name = "status", columnDefinition = "ENUM('PENDING', 'ACTIVE', 'SUSPENDED', 'TERMINATED')")
    private String status;

    @Column(name = "bill_day", nullable = false, columnDefinition = "TINYINT")
    private Integer billDay;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}