
package com.example.smartfarming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smartfarming.entity.WeatherRecord;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {}
