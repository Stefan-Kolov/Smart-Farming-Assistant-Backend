package com.smartfarmingassistant.sfa.repository;

import com.smartfarmingassistant.sfa.model.domain.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {}
