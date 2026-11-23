package com.yojit.bss.sim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimCardResponse {
    private Long id;
    private String msisdn;
    private String iccid;
    private String status;
}