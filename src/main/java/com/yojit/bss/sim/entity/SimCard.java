package com.yojit.bss.sim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sim_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String msisdn;

    @Column(nullable = false, unique = true, length = 22)
    private String iccid;

    @Column(name = "status", columnDefinition = "ENUM('AVAILABLE', 'ASSIGNED', 'BLOCKED')")
    private String status;
}