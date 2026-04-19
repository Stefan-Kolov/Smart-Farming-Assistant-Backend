package com.smartfarmingassistant.sfa.repository;

import com.smartfarmingassistant.sfa.model.domain.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CropRepository extends JpaRepository<Crop, Long> {}
