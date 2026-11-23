package com.yojit.bss.sim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SimCardRequest {

    @NotBlank(message = "MSISDN is required")
    @Size(max = 15, message = "MSISDN max 15 chars")
    private String msisdn;

    @NotBlank(message = "ICCID is required")
    @Size(max = 22, message = "ICCID max 22 chars")
    private String iccid;

    public SimCardRequest() {}

    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }

    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
}