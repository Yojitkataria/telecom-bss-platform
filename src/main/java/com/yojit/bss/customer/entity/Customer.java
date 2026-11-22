package com.yojit.bss.customer.entity;

import com.yojit.bss.customer.enums.KycStatus;
import com.yojit.bss.security.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "city", length = 80)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", length = 20)
    private KycStatus kycStatus;

    @Column(name = "kyc_document_type", length = 30)
    private String kycDocumentType;

    @Column(name = "kyc_document_no", length = 40)
    private String kycDocumentNo;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}