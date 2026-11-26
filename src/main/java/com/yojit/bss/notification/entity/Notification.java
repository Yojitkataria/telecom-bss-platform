package com.yojit.bss.notification.entity;

import com.yojit.bss.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, columnDefinition = "ENUM('EMAIL', 'SMS')")
    private String channel;

    @Column(length = 150)
    private String subject;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, columnDefinition = "ENUM('SENT', 'FAILED')")
    private String status;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}