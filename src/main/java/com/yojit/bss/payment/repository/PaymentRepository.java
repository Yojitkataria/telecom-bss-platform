package com.yojit.bss.payment.repository;

import com.yojit.bss.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTxnRef(String txnRef);
    List<Payment> findByInvoiceId(Long invoiceId);
}