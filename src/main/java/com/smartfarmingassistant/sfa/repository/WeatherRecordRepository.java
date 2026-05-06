package com.smartfarmingassistant.sfa.repository;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.entity.WeatherRecord;
import com.smartfarmingassistant.sfa.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {
    List<WeatherRecord> findAllByFarmIdAndFarmUserOrderByRecordedAtDesc(Long farmId, User user);
}
