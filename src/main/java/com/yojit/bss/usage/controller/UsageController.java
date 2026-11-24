package com.yojit.bss.usage.controller;

import com.yojit.bss.usage.dto.RecordUsageRequest;
import com.yojit.bss.usage.dto.UsageSummaryResponse;
import com.yojit.bss.usage.service.UsageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageService usageService;

    @PostMapping("/record")
    public ResponseEntity<String> recordUsage(@Valid @RequestBody RecordUsageRequest request) {
        usageService.recordUsage(request);
        return ResponseEntity.ok("Usage record ingested successfully");
    }

    @GetMapping("/summary/{subscriptionId}")
    public ResponseEntity<UsageSummaryResponse> getUsageSummary(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(usageService.getUsageSummary(subscriptionId));
    }
}