package com.yojit.bss.usage.service;

import com.yojit.bss.addon.entity.SubscriptionAddon;
import com.yojit.bss.addon.service.AddonService;
import com.yojit.bss.catalog.entity.Plan;
import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.subscription.entity.Subscription;
import com.yojit.bss.subscription.repository.SubscriptionRepository;
import com.yojit.bss.usage.dto.RecordUsageRequest;
import com.yojit.bss.usage.dto.UsageSummaryResponse;
import com.yojit.bss.usage.entity.UsageRecord;
import com.yojit.bss.usage.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageService {

    private final UsageRecordRepository usageRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AddonService addonService;

    @Transactional
    public void recordUsage(RecordUsageRequest request) {
        if (request.getUsageType() == null) {
            throw new IllegalArgumentException("Usage type cannot be null");
        }

        String type = request.getUsageType().toUpperCase();
        if (!List.of("VOICE", "SMS", "DATA").contains(type)) {
            throw new IllegalArgumentException("Invalid usage type: " + request.getUsageType() + ". Allowed types: VOICE, SMS, DATA");
        }

        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found ID: " + request.getSubscriptionId()));

        UsageRecord record = UsageRecord.builder()
                .subscription(subscription)
                .usageType(type)
                .quantity(request.getQuantity())
                .usedAt(LocalDateTime.now())
                .build();

        usageRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public UsageSummaryResponse getUsageSummary(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found ID: " + subscriptionId));

        Plan plan = subscription.getPlan();
        LocalDateTime billingCycleStart = subscription.getActivatedAt() != null 
                ? subscription.getActivatedAt() 
                : (subscription.getCreatedAt() != null ? subscription.getCreatedAt() : LocalDateTime.now().minusDays(30));

        // 1. Fetch Base Allowances with null checks
        int totalDataAllowed = plan.getDataMb() != null ? plan.getDataMb() : 0;
        int totalVoiceAllowed = plan.getVoiceMinutes() != null ? plan.getVoiceMinutes() : 0;
        int totalSmsAllowed = plan.getSmsCount() != null ? plan.getSmsCount() : 0;

        // 2. Add-on Allowances
        List<SubscriptionAddon> activeAddons = addonService.getActiveAddonsForSubscription(subscriptionId);
        if (activeAddons != null) {
            for (SubscriptionAddon subAddon : activeAddons) {
                if (subAddon.getAddon() != null) {
                    totalDataAllowed += subAddon.getAddon().getExtraDataMb() != null ? subAddon.getAddon().getExtraDataMb() : 0;
                    totalVoiceAllowed += subAddon.getAddon().getExtraVoiceMinutes() != null ? subAddon.getAddon().getExtraVoiceMinutes() : 0;
                    totalSmsAllowed += subAddon.getAddon().getExtraSms() != null ? subAddon.getAddon().getExtraSms() : 0;
                }
            }
        }

        // 3. Fetch Actual Consumption (Null-Safe)
        BigDecimal rawData = usageRecordRepository.getTotalUsageSince(subscriptionId, "DATA", billingCycleStart);
        BigDecimal rawVoice = usageRecordRepository.getTotalUsageSince(subscriptionId, "VOICE", billingCycleStart);
        BigDecimal rawSms = usageRecordRepository.getTotalUsageSince(subscriptionId, "SMS", billingCycleStart);

        BigDecimal dataUsed = rawData != null ? rawData : BigDecimal.ZERO;
        BigDecimal voiceUsed = rawVoice != null ? rawVoice : BigDecimal.ZERO;
        BigDecimal smsUsed = rawSms != null ? rawSms : BigDecimal.ZERO;

        // 4. Calculate Overages safely
        BigDecimal dataOverage = dataUsed.subtract(BigDecimal.valueOf(totalDataAllowed)).max(BigDecimal.ZERO);
        BigDecimal voiceOverage = voiceUsed.subtract(BigDecimal.valueOf(totalVoiceAllowed)).max(BigDecimal.ZERO);
        BigDecimal smsOverage = smsUsed.subtract(BigDecimal.valueOf(totalSmsAllowed)).max(BigDecimal.ZERO);

        return UsageSummaryResponse.builder()
                .subscriptionId(subscriptionId)
                .planName(plan.getName())
                .dataUsedMb(dataUsed)
                .dataAllowedMb(totalDataAllowed)
                .dataOverageMb(dataOverage)
                .voiceUsedMinutes(voiceUsed)
                .voiceAllowedMinutes(totalVoiceAllowed)
                .voiceOverageMinutes(voiceOverage)
                .smsUsed(smsUsed)
                .smsAllowed(totalSmsAllowed)
                .smsOverage(smsOverage)
                .build();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal calculateOverageCharge(Long subscriptionId, LocalDateTime cycleStart) {
        UsageSummaryResponse summary = getUsageSummary(subscriptionId);
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found ID: " + subscriptionId));
        
        Plan plan = subscription.getPlan();

        BigDecimal dataCost = summary.getDataOverageMb().multiply(plan.getOverageDataPerMb());
        BigDecimal voiceCost = summary.getVoiceOverageMinutes().multiply(plan.getOverageVoicePerMin());
        BigDecimal smsCost = summary.getSmsOverage().multiply(plan.getOverageSmsEach());

        return dataCost.add(voiceCost).add(smsCost);
    }
}