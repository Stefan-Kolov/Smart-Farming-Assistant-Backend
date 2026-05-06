package com.smartfarmingassistant.sfa.repository;

import java.util.List;
import java.util.Optional;

import com.smartfarmingassistant.sfa.model.domain.entity.Crop;
import com.smartfarmingassistant.sfa.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findAllByFarmIdAndFarmUser(Long farmId, User user);

    Optional<Crop> findByIdAndFarmUser(Long id, User user);
}
