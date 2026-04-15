
package com.example.smartfarming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smartfarming.entity.Farm;

public interface FarmRepository extends JpaRepository<Farm, Long> {}
