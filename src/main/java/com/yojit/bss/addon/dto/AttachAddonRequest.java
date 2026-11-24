package com.yojit.bss.addon.dto;

import jakarta.validation.constraints.NotNull;

public class AttachAddonRequest {

    @NotNull(message = "Subscription ID is required")
    private Long subscriptionId;

    @NotNull(message = "Add-on ID is required")
    private Long addonId;

    public AttachAddonRequest() {}

    public Long getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }

    public Long getAddonId() { return addonId; }
    public void setAddonId(Long addonId) { this.addonId = addonId; }
}