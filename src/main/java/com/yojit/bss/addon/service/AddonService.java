package com.yojit.bss.addon.service;

import com.yojit.bss.addon.dto.AddonResponse;
import com.yojit.bss.addon.dto.AttachAddonRequest;
import com.yojit.bss.addon.entity.Addon;
import com.yojit.bss.addon.entity.SubscriptionAddon;
import com.yojit.bss.addon.repository.AddonRepository;
import com.yojit.bss.addon.repository.SubscriptionAddonRepository;
import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.subscription.entity.Subscription;
import com.yojit.bss.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddonService {

    private final AddonRepository addonRepository;
    private final SubscriptionAddonRepository subscriptionAddonRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<AddonResponse> getActiveAddons() {
        return addonRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void attachAddonToSubscription(AttachAddonRequest request) {
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Addon addon = addonRepository.findById(request.getAddonId())
                .orElseThrow(() -> new ResourceNotFoundException("Add-on not found"));

        SubscriptionAddon subAddon = SubscriptionAddon.builder()
                .subscription(subscription)
                .addon(addon)
                .addedOn(LocalDate.now())
                .build();

        subscriptionAddonRepository.save(subAddon);
    }

    public List<SubscriptionAddon> getActiveAddonsForSubscription(Long subscriptionId) {
        return subscriptionAddonRepository.findBySubscriptionIdAndRemovedOnIsNull(subscriptionId);
    }

    private AddonResponse mapToResponse(Addon addon) {
        return AddonResponse.builder()
                .id(addon.getId())
                .name(addon.getName())
                .description(addon.getDescription())
                .price(addon.getPrice())
                .extraDataMb(addon.getExtraDataMb())
                .extraVoiceMinutes(addon.getExtraVoiceMinutes())
                .extraSms(addon.getExtraSms())
                .active(addon.getActive())
                .build();
    }
}