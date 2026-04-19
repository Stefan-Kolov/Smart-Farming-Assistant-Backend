package com.smartfarmingassistant.sfa.repository;

import com.smartfarmingassistant.sfa.model.domain.entity.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {}
