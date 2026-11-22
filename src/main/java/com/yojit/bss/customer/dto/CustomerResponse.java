package com.yojit.bss.customer.dto;

import com.yojit.bss.customer.enums.KycStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private KycStatus kycStatus;
    private String kycDocumentType;
    private String kycDocumentNo;
}