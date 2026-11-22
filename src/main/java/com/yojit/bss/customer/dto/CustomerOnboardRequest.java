package com.yojit.bss.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CustomerOnboardRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    private String address;
    private String city;
    private String kycDocumentType;
    private String kycDocumentNo;

    public CustomerOnboardRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getKycDocumentType() { return kycDocumentType; }
    public void setKycDocumentType(String kycDocumentType) { this.kycDocumentType = kycDocumentType; }

    public String getKycDocumentNo() { return kycDocumentNo; }
    public void setKycDocumentNo(String kycDocumentNo) { this.kycDocumentNo = kycDocumentNo; }
}