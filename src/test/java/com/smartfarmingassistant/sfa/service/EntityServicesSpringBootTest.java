package com.smartfarmingassistant.sfa.service;

import java.time.LocalDate;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.crop.CropCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.model.enums.SoilType;
import com.smartfarmingassistant.sfa.model.exception.ResourceNotFoundException;
import com.smartfarmingassistant.sfa.repository.UserRepository;
import com.smartfarmingassistant.sfa.service.domain.CropService;
import com.smartfarmingassistant.sfa.service.domain.FarmService;
import com.smartfarmingassistant.sfa.testutil.PostgresTestContainerBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class EntityServicesSpringBootTest extends PostgresTestContainerBase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmService farmService;

    @Autowired
    private CropService cropService;

    @Test
    void userCannotAccessOtherUsersFarmOrCrop() {
        User u1 = userRepository.save(new User("A", "A", "a4@a.com", "u1111", "pw"));
        User u2 = userRepository.save(new User("B", "B", "b4@b.com", "u2222", "pw"));

        Long farmId = farmService.create(u1, new FarmCreateRequest("F", "L")).id();
        Long cropId = cropService.createUnderFarm(
                u1,
                farmId,
                new CropCreateRequest("C", LocalDate.parse("2026-05-01"), SoilType.CLAY)
        ).id();

        assertThatThrownBy(() -> farmService.get(u2, farmId))
                .isInstanceOf(ResourceNotFoundException.class);

        assertThatThrownBy(() -> cropService.get(u2, cropId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
