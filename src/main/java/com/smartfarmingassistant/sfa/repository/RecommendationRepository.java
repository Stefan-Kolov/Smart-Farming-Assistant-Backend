
package com.example.smartfarming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smartfarming.entity.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {}
