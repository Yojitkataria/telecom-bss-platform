package com.yojit.bss.subscription.controller;

import com.yojit.bss.subscription.dto.SubscribeRequest;
import com.yojit.bss.subscription.dto.SubscriptionResponse;
import com.yojit.bss.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SubscribeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionService.createSubscription(userDetails.getUsername(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(subscriptionService.getMySubscriptions(userDetails.getUsername()));
    }
}