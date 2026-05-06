package com.smartfarmingassistant.sfa.repository;

import java.util.List;
import java.util.Optional;

import com.smartfarmingassistant.sfa.model.domain.entity.Farm;
import com.smartfarmingassistant.sfa.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findAllByUser(User user);

    Optional<Farm> findByIdAndUser(Long id, User user);
}
