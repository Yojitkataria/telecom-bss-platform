package com.yojit.bss.customer.controller;

import com.yojit.bss.customer.dto.CustomerOnboardRequest;
import com.yojit.bss.customer.dto.CustomerResponse;
import com.yojit.bss.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/me")
    public ResponseEntity<CustomerResponse> onboardMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CustomerOnboardRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.onboardCustomer(userDetails.getUsername(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(customerService.getMyProfile(userDetails.getUsername()));
    }
}