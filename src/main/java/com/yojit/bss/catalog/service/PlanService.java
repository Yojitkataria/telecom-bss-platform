package com.yojit.bss.catalog.service;

import com.yojit.bss.catalog.dto.CreatePlanRequest;
import com.yojit.bss.catalog.dto.PlanResponse;
import com.yojit.bss.catalog.entity.Plan;
import com.yojit.bss.catalog.repository.PlanRepository;
import com.yojit.bss.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class PlanService{
	
	
	
	private final PlanRepository planRepository;
	private final ModelMapper modelMapper;
	public List<PlanResponse> getAllActivePlans(){
		
		
		return planRepository.findByActiveTrue()
				.stream()
				.map(plan-> modelMapper.map(plan,PlanResponse.class))
				.toList();
	}
	
	
	public PlanResponse getPlanById(Long id) {
		
		Plan plan = planRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
		return modelMapper.map(plan,PlanResponse.class);
	}
	

	@Transactional
	public PlanResponse createPlan(CreatePlanRequest request) {
    
		Plan plan = modelMapper.map(request,Plan.class);
		
		Plan savedPlan = planRepository.save(plan);
		
		return modelMapper.map(savedPlan, PlanResponse.class);
    }
}