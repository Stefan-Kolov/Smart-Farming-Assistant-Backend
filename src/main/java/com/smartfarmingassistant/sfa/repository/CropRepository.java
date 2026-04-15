
package com.example.smartfarming.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.smartfarming.entity.Crop;

public interface CropRepository extends JpaRepository<Crop, Long> {}
