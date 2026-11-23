package com.yojit.bss.subscription.service;

import com.yojit.bss.catalog.entity.Plan;
import com.yojit.bss.catalog.repository.PlanRepository;
import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.customer.entity.Customer;
import com.yojit.bss.customer.repository.CustomerRepository;
import com.yojit.bss.sim.entity.SimCard;
import com.yojit.bss.sim.repository.SimCardRepository;
import com.yojit.bss.subscription.dto.SubscribeRequest;
import com.yojit.bss.subscription.dto.SubscriptionResponse;
import com.yojit.bss.subscription.entity.Subscription;
import com.yojit.bss.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final SimCardRepository simCardRepository;

    @Transactional
    public SubscriptionResponse createSubscription(String userEmail, SubscribeRequest request) {
        Customer customer = customerRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found. Please complete KYC onboarding first."));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + request.getPlanId()));

        SimCard simCard = simCardRepository.findById(request.getSimId())
                .orElseThrow(() -> new ResourceNotFoundException("SIM card not found with ID: " + request.getSimId()));

        if (!"AVAILABLE".equals(simCard.getStatus())) {
            throw new IllegalArgumentException("SIM card is not available for subscription");
        }

        // Auto-assign SIM status to ASSIGNED
        simCard.setStatus("ASSIGNED");
        simCardRepository.save(simCard);

        Subscription subscription = Subscription.builder()
                .customer(customer)
                .plan(plan)
                .simCard(simCard)
                .status("ACTIVE")
                .billDay(LocalDate.now().getDayOfMonth())
                .activatedAt(LocalDateTime.now())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    public List<SubscriptionResponse> getMySubscriptions(String userEmail) {
        Customer customer = customerRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        return subscriptionRepository.findByCustomerId(customer.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SubscriptionResponse mapToResponse(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .customerId(sub.getCustomer().getId())
                .customerName(sub.getCustomer().getFullName())
                .planName(sub.getPlan().getName())
                .msisdn(sub.getSimCard().getMsisdn())
                .iccid(sub.getSimCard().getIccid())
                .status(sub.getStatus())
                .billDay(sub.getBillDay())
                .activatedAt(sub.getActivatedAt())
                .createdAt(sub.getCreatedAt())
                .build();
    }
}