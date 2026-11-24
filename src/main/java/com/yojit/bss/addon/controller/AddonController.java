package com.yojit.bss.addon.controller;

import com.yojit.bss.addon.dto.AddonResponse;
import com.yojit.bss.addon.dto.AttachAddonRequest;
import com.yojit.bss.addon.service.AddonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addons")
@RequiredArgsConstructor
public class AddonController {

    private final AddonService addonService;

    @GetMapping
    public ResponseEntity<List<AddonResponse>> getActiveAddons() {
        return ResponseEntity.ok(addonService.getActiveAddons());
    }

    @PostMapping("/attach")
    public ResponseEntity<String> attachAddon(@Valid @RequestBody AttachAddonRequest request) {
        addonService.attachAddonToSubscription(request);
        return ResponseEntity.ok("Add-on successfully attached to subscription");
    }
}