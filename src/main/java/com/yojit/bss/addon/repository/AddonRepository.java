package com.yojit.bss.addon.repository;

import com.yojit.bss.addon.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddonRepository extends JpaRepository<Addon, Long> {
    List<Addon> findByActiveTrue();
}