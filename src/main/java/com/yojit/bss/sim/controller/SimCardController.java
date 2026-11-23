package com.yojit.bss.sim.controller;

import com.yojit.bss.sim.dto.SimCardRequest;
import com.yojit.bss.sim.dto.SimCardResponse;
import com.yojit.bss.sim.service.SimCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sims")
@RequiredArgsConstructor
public class SimCardController {

    private final SimCardService simCardService;

    @PostMapping
    public ResponseEntity<SimCardResponse> addSimCard(@Valid @RequestBody SimCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(simCardService.addSimCard(request));
    }

    @GetMapping
    public ResponseEntity<List<SimCardResponse>> getAllSimCards() {
        return ResponseEntity.ok(simCardService.getAllSimCards());
    }
}