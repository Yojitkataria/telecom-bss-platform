package com.yojit.bss.catalog.controller;

import com.yojit.bss.catalog.dto.CreatePlanRequest;
import com.yojit.bss.catalog.dto.PlanResponse;
import com.yojit.bss.catalog.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor

public class PlanController{
	
	private final PlanService planService;
	
	@GetMapping
	public ResponseEntity<List<PlanResponse>> getAllPlans(){
		
		
		return ResponseEntity.ok(planService.getAllActivePlans());
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long id){
		
		return ResponseEntity.ok(planService.getPlanById(id));
	}
	
	//ADMIN-only
	@PostMapping
	
	public ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request){
		
		return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlan(request));
	}
}