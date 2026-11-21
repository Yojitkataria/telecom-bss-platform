package com.yojit.bss.catalog.dto;

import java.math.BigDecimal;

public class PlanResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal monthlyFee;
    private Integer dataMb;
    private Integer voiceMinutes;
    private Integer smsCount;
    private Boolean active;

    // Required by ModelMapper
    public PlanResponse() {
    }

    public PlanResponse(Long id, String name, String description, BigDecimal monthlyFee, 
                        Integer dataMb, Integer voiceMinutes, Integer smsCount, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.monthlyFee = monthlyFee;
        this.dataMb = dataMb;
        this.voiceMinutes = voiceMinutes;
        this.smsCount = smsCount;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(BigDecimal monthlyFee) { this.monthlyFee = monthlyFee; }

    public Integer getDataMb() { return dataMb; }
    public void setDataMb(Integer dataMb) { this.dataMb = dataMb; }

    public Integer getVoiceMinutes() { return voiceMinutes; }
    public void setVoiceMinutes(Integer voiceMinutes) { this.voiceMinutes = voiceMinutes; }

    public Integer getSmsCount() { return smsCount; }
    public void setSmsCount(Integer smsCount) { this.smsCount = smsCount; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}