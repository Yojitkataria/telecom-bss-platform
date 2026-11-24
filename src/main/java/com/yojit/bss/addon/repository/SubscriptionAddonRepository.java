package com.yojit.bss.addon.repository;

import com.yojit.bss.addon.entity.SubscriptionAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionAddonRepository extends JpaRepository<SubscriptionAddon, Long> {
    List<SubscriptionAddon> findBySubscriptionIdAndRemovedOnIsNull(Long subscriptionId);
}