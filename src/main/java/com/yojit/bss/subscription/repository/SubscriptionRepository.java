package com.yojit.bss.subscription.repository;

import com.yojit.bss.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByCustomerId(Long customerId);
    Optional<Subscription> findBySimCardId(Long simId);
    boolean existsBySimCardIdAndStatusNot(Long simId, String status);
}