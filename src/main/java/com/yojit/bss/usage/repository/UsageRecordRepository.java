package com.yojit.bss.usage.repository;

import com.yojit.bss.usage.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

    List<UsageRecord> findBySubscriptionId(Long subscriptionId);

    @Query("SELECT COALESCE(SUM(u.quantity), 0) FROM UsageRecord u " +
           "WHERE u.subscription.id = :subscriptionId " +
           "AND u.usageType = :usageType " +
           "AND u.usedAt >= :startDate")
    BigDecimal getTotalUsageSince(@Param("subscriptionId") Long subscriptionId,
                                  @Param("usageType") String usageType,
                                  @Param("startDate") LocalDateTime startDate);
}