package com.yojit.bss.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="plans")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Plan{
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=80)
	private String name;
	
	@Column(length=255)
	private String description;
	
	@Column(name="monthly_fee",nullable = false , precision = 10,scale = 2)
	private BigDecimal monthlyFee;
	
	@Column(name = "data_mb", nullable = false)
	private Integer dataMb;
	
	@Column(name = "voice_minutes",nullable = false)
	private Integer voiceMinutes;
	
	@Column(name="sms_count",nullable = false)
	private Integer smsCount;
	
	@Column(name = "overage_data_per_mb",nullable = false, precision = 8 , scale = 4)
	private BigDecimal overageDataPerMb;
	
	
	@Column(name = "overage_voice_per_min",nullable = false, precision = 8 , scale = 4)
	private BigDecimal overageVoicePerMin;
	
	@Column(name = "overage_sms_each",nullable = false , precision = 8 , scale = 4)
    private BigDecimal overageSmsEach;
	
	@Column(nullable = false)
	private Boolean active;


	
	
	
	
}