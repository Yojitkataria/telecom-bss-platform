package com.yojit.bss.billing.controller;

import com.yojit.bss.billing.dto.GenerateInvoiceRequest;
import com.yojit.bss.billing.dto.InvoiceResponse;
import com.yojit.bss.billing.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/generate")
    public ResponseEntity<InvoiceResponse> generateInvoice(@Valid @RequestBody GenerateInvoiceRequest request) {
        return ResponseEntity.ok(billingService.generateInvoice(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getInvoiceById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(billingService.getInvoicesByCustomer(customerId));
    }
}