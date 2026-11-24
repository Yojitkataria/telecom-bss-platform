package com.yojit.bss.addon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "addons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Addon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "extra_data_mb", nullable = false)
    private Integer extraDataMb;

    @Column(name = "extra_voice_minutes", nullable = false)
    private Integer extraVoiceMinutes;

    @Column(name = "extra_sms", nullable = false)
    private Integer extraSms;

    @Column(nullable = false)
    private Boolean active;
}