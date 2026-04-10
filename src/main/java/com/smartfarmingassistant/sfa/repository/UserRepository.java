package com.smartfarmingassistant.sfa.repository;

import java.util.List;
import java.util.Optional;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.projection.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select name, surname, email from users", nativeQuery = true)
    List<UserProjection> findAllWithNameSurnameAndEmail();

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
