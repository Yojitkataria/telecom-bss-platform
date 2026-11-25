package com.yojit.bss.billing.repository;

import com.yojit.bss.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findBySubscriptionIdAndPeriodStartAndPeriodEnd(Long subscriptionId, LocalDate periodStart, LocalDate periodEnd);

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findBySubscriptionId(Long subscriptionId);
}