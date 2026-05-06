package com.smartfarmingassistant.sfa.repository;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.entity.Recommendation;
import com.smartfarmingassistant.sfa.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findAllByFarmIdAndFarmUserOrderByCreatedAtDesc(Long farmId, User user);
}
