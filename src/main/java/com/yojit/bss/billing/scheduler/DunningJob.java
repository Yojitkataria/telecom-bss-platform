package com.yojit.bss.billing.scheduler;

import com.yojit.bss.billing.entity.Invoice;
import com.yojit.bss.billing.repository.InvoiceRepository;
import com.yojit.bss.notification.service.NotificationService;
import com.yojit.bss.subscription.entity.Subscription;
import com.yojit.bss.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DunningJob {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    // Runs every day at 01:00 AM
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void executeDunningProcess() {
        log.info("Starting automated dunning job for overdue invoices...");

        LocalDate today = LocalDate.now();

        // 1. Transition newly overdue invoices (GENERATED -> OVERDUE)
        List<Invoice> newlyOverdueInvoices = invoiceRepository.findByStatusAndDueDateBefore("GENERATED", today);

        for (Invoice invoice : newlyOverdueInvoices) {
            invoice.setStatus("OVERDUE");
            invoiceRepository.save(invoice);

            Subscription sub = invoice.getSubscription();
            if ("ACTIVE".equalsIgnoreCase(sub.getStatus())) {
                sub.setStatus("SUSPENDED");
                subscriptionRepository.save(sub);
            }

            notificationService.sendNotification(
                    invoice.getCustomer(),
                    "SMS",
                    "Account Suspended - Overdue Bill",
                    "Your invoice " + invoice.getInvoiceNo() + " is overdue. Subscription suspended. Please pay ₹" + invoice.getTotalAmount() + " immediately."
            );
        }

        // 2. Send follow-up reminders for existing OVERDUE invoices
        List<Invoice> existingOverdueInvoices = invoiceRepository.findByStatusAndDueDateBefore("OVERDUE", today);

        for (Invoice invoice : existingOverdueInvoices) {
            notificationService.sendNotification(
                    invoice.getCustomer(),
                    "EMAIL",
                    "Reminder: Overdue Invoice " + invoice.getInvoiceNo(),
                    "Your account remains suspended due to overdue invoice " + invoice.getInvoiceNo() + ". Amount due: ₹" + invoice.getTotalAmount()
            );
        }

        log.info("Dunning job finished. Newly suspended: {}, Reminders sent: {}", 
                newlyOverdueInvoices.size(), existingOverdueInvoices.size());
    }
}