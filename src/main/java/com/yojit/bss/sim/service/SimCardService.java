package com.yojit.bss.sim.service;

import com.yojit.bss.common.exception.ResourceNotFoundException;
import com.yojit.bss.sim.dto.SimCardRequest;
import com.yojit.bss.sim.dto.SimCardResponse;
import com.yojit.bss.sim.entity.SimCard;
import com.yojit.bss.sim.repository.SimCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimCardService {

    private final SimCardRepository simCardRepository;

    @Transactional
    public SimCardResponse addSimCard(SimCardRequest request) {
        if (simCardRepository.existsByMsisdn(request.getMsisdn())) {
            throw new IllegalArgumentException("MSISDN already exists: " + request.getMsisdn());
        }
        if (simCardRepository.existsByIccid(request.getIccid())) {
            throw new IllegalArgumentException("ICCID already exists: " + request.getIccid());
        }

        SimCard simCard = SimCard.builder()
                .msisdn(request.getMsisdn())
                .iccid(request.getIccid())
                .status("AVAILABLE")
                .build();

        SimCard saved = simCardRepository.save(simCard);
        return mapToResponse(saved);
    }

    public List<SimCardResponse> getAllSimCards() {
        return simCardRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SimCardResponse mapToResponse(SimCard simCard) {
        return SimCardResponse.builder()
                .id(simCard.getId())
                .msisdn(simCard.getMsisdn())
                .iccid(simCard.getIccid())
                .status(simCard.getStatus())
                .build();
    }
}