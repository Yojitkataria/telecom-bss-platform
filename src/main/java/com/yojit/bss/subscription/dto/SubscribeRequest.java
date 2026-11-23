package com.yojit.bss.subscription.dto;

import jakarta.validation.constraints.NotNull;

public class SubscribeRequest {

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotNull(message = "SIM ID is required")
    private Long simId;

    public SubscribeRequest() {}

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Long getSimId() { return simId; }
    public void setSimId(Long simId) { this.simId = simId; }
}