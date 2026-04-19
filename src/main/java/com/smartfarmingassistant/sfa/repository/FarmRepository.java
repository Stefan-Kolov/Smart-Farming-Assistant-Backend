package com.smartfarmingassistant.sfa.repository;

import com.smartfarmingassistant.sfa.model.domain.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FarmRepository extends JpaRepository<Farm, Long> {}
