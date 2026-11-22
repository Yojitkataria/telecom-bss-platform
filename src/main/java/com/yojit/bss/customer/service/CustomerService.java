package com.yojit.bss.customer.service;

import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.customer.dto.CustomerOnboardRequest;
import com.yojit.bss.customer.dto.CustomerResponse;
import com.yojit.bss.customer.entity.Customer;
import com.yojit.bss.customer.enums.KycStatus;
import com.yojit.bss.customer.repository.CustomerRepository;
import com.yojit.bss.security.entity.User;
import com.yojit.bss.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional
    public CustomerResponse onboardCustomer(String userEmail, CustomerOnboardRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (customerRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalArgumentException("Customer profile already exists for this user");
        }

        Customer customer = Customer.builder()
                .user(user)
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .kycStatus(KycStatus.PENDING)
                .kycDocumentType(request.getKycDocumentType())
                .kycDocumentNo(request.getKycDocumentNo())
                .build();

        Customer saved = customerRepository.save(customer);
        return mapToResponse(saved);
    }

    public CustomerResponse getMyProfile(String userEmail) {
        Customer customer = customerRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for email: " + userEmail));
        return mapToResponse(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getUser().getEmail())
                .fullName(customer.getFullName())
                .dateOfBirth(customer.getDateOfBirth())
                .address(customer.getAddress())
                .city(customer.getCity())
                .kycStatus(customer.getKycStatus())
                .kycDocumentType(customer.getKycDocumentType())
                .kycDocumentNo(customer.getKycDocumentNo())
                .build();
    }
}