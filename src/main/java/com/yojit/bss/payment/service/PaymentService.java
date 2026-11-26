package com.yojit.bss.payment.service;

import com.yojit.bss.billing.entity.Invoice;
import com.yojit.bss.billing.repository.InvoiceRepository;
import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.notification.service.NotificationService;
import com.yojit.bss.payment.dto.ProcessPaymentRequest;
import com.yojit.bss.payment.dto.PaymentResponse;
import com.yojit.bss.payment.entity.Payment;
import com.yojit.bss.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        String method = request.getMethod().toUpperCase();
        if (!List.of("UPI", "CARD", "NETBANKING").contains(method)) {
            throw new IllegalArgumentException("Invalid payment method: " + request.getMethod() + ". Allowed: UPI, CARD, NETBANKING");
        }

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found ID: " + request.getInvoiceId()));

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalStateException("Invoice " + invoice.getInvoiceNo() + " is already paid");
        }

        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .method(method)
                .status("SUCCESS")
                .txnRef(txnRef)
                .paidAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update Invoice status to PAID
        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        // Dispatch Notification
        notificationService.sendNotification(
                invoice.getCustomer(),
                "EMAIL",
                "Payment Received for Invoice " + invoice.getInvoiceNo(),
                "Thank you! Your payment of ₹" + request.getAmount() + " via " + method + " (Txn Ref: " + txnRef + ") was successful."
        );

        return mapToResponse(savedPayment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNo(payment.getInvoice().getInvoiceNo())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .txnRef(payment.getTxnRef())
                .paidAt(payment.getPaidAt())
                .build();
    }
}