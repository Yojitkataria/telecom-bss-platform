package com.yojit.bss.sim.repository;

import com.yojit.bss.sim.entity.SimCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SimCardRepository extends JpaRepository<SimCard, Long> {
    Optional<SimCard> findFirstByStatus(String status);
    Optional<SimCard> findByMsisdn(String msisdn);
    Optional<SimCard> findByIccid(String iccid);
    boolean existsByMsisdn(String msisdn);
    boolean existsByIccid(String iccid);
}