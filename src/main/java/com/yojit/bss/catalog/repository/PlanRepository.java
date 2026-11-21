package com.yojit.bss.catalog.repository;

import com.yojit.bss.catalog.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;




@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>{
	
	List<Plan> findByActiveTrue();
	
	
}

