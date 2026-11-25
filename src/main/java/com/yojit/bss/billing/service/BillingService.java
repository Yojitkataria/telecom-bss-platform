package com.yojit.bss.billing.service;

import com.yojit.bss.addon.entity.SubscriptionAddon;
import com.yojit.bss.addon.service.AddonService;
import com.yojit.bss.billing.dto.GenerateInvoiceRequest;
import com.yojit.bss.billing.dto.InvoiceResponse;
import com.yojit.bss.billing.entity.Invoice;
import com.yojit.bss.billing.entity.InvoiceItem;
import com.yojit.bss.billing.repository.InvoiceItemRepository;
import com.yojit.bss.billing.repository.InvoiceRepository;
import com.yojit.bss.catalog.entity.Plan;
import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.subscription.entity.Subscription;
import com.yojit.bss.subscription.repository.SubscriptionRepository;
import com.yojit.bss.usage.dto.UsageSummaryResponse;
import com.yojit.bss.usage.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AddonService addonService;
    private final UsageService usageService;

    private static final BigDecimal GST_RATE = new BigDecimal("0.18"); // 18% GST

    @Transactional
    public InvoiceResponse generateInvoice(GenerateInvoiceRequest request) {
        // Idempotency check: Return existing invoice if already generated for this period
        return invoiceRepository.findBySubscriptionIdAndPeriodStartAndPeriodEnd(
                request.getSubscriptionId(), request.getPeriodStart(), request.getPeriodEnd())
                .map(this::mapToResponse)
                .orElseGet(() -> createNewInvoice(request));
    }

    private InvoiceResponse createNewInvoice(GenerateInvoiceRequest request) {
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found ID: " + request.getSubscriptionId()));

        Plan plan = subscription.getPlan();

        // 1. Calculate Plan Charge
        BigDecimal planCharge = plan.getMonthlyFee() != null ? plan.getMonthlyFee() : BigDecimal.ZERO;

        // 2. Calculate Add-on Charges
        BigDecimal addonCharge = BigDecimal.ZERO;
        List<InvoiceItem> lineItems = new ArrayList<>();

        // Base Plan item
        lineItems.add(InvoiceItem.builder()
                .itemType("PLAN")
                .description("Base Plan: " + plan.getName())
                .amount(planCharge)
                .build());

        List<SubscriptionAddon> activeAddons = addonService.getActiveAddonsForSubscription(subscription.getId());
        if (activeAddons != null) {
            for (SubscriptionAddon subAddon : activeAddons) {
                if (subAddon.getAddon() != null) {
                    BigDecimal price = subAddon.getAddon().getPrice() != null ? subAddon.getAddon().getPrice() : BigDecimal.ZERO;
                    addonCharge = addonCharge.add(price);

                    lineItems.add(InvoiceItem.builder()
                            .itemType("ADDON")
                            .description("Add-on: " + subAddon.getAddon().getName())
                            .amount(price)
                            .build());
                }
            }
        }

        // 3. Calculate Overage Charges
        UsageSummaryResponse summary = usageService.getUsageSummary(subscription.getId());
        BigDecimal dataOverageCost = summary.getDataOverageMb().multiply(plan.getOverageDataPerMb());
        BigDecimal voiceOverageCost = summary.getVoiceOverageMinutes().multiply(plan.getOverageVoicePerMin());
        BigDecimal smsOverageCost = summary.getSmsOverage().multiply(plan.getOverageSmsEach());

        BigDecimal overageCharge = dataOverageCost.add(voiceOverageCost).add(smsOverageCost);
        if (overageCharge.compareTo(BigDecimal.ZERO) > 0) {
            lineItems.add(InvoiceItem.builder()
                    .itemType("OVERAGE")
                    .description("Usage Overage Charges (Data/Voice/SMS)")
                    .amount(overageCharge)
                    .build());
        }

        // 4. Subtotal & GST Tax (18%)
        BigDecimal subtotal = planCharge.add(addonCharge).add(overageCharge);
        BigDecimal taxAmount = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        lineItems.add(InvoiceItem.builder()
                .itemType("TAX")
                .description("GST (18%)")
                .amount(taxAmount)
                .build());

        // 5. Build and Save Invoice
        String invoiceNo = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate today = LocalDate.now();

        Invoice invoice = Invoice.builder()
                .invoiceNo(invoiceNo)
                .subscription(subscription)
                .customer(subscription.getCustomer())
                .periodStart(request.getPeriodStart())
                .periodEnd(request.getPeriodEnd())
                .planCharge(planCharge)
                .addonCharge(addonCharge)
                .overageCharge(overageCharge)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .status("GENERATED")
                .issuedOn(today)
                .dueDate(today.plusDays(15))
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Attach saved invoice reference to items and save
        lineItems.forEach(item -> item.setInvoice(savedInvoice));
        invoiceItemRepository.saveAll(lineItems);

        return mapToResponse(savedInvoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found ID: " + invoiceId));
        return mapToResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceId(invoice.getId());

        List<InvoiceResponse.InvoiceItemDto> itemDtos = items.stream()
                .map(item -> new InvoiceResponse.InvoiceItemDto(item.getItemType(), item.getDescription(), item.getAmount()))
                .toList();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNo(invoice.getInvoiceNo())
                .subscriptionId(invoice.getSubscription().getId())
                .customerId(invoice.getCustomer().getId())
                .periodStart(invoice.getPeriodStart())
                .periodEnd(invoice.getPeriodEnd())
                .planCharge(invoice.getPlanCharge())
                .addonCharge(invoice.getAddonCharge())
                .overageCharge(invoice.getOverageCharge())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .issuedOn(invoice.getIssuedOn())
                .dueDate(invoice.getDueDate())
                .items(itemDtos)
                .build();
    }
}