package com.smartfarmingassistant.sfa.service;

import java.time.LocalDate;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.crop.CropCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.crop.CropUpdateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmUpdateRequest;
import com.smartfarmingassistant.sfa.model.enums.SoilType;
import com.smartfarmingassistant.sfa.model.exception.ResourceNotFoundException;
import com.smartfarmingassistant.sfa.repository.CropRepository;
import com.smartfarmingassistant.sfa.repository.FarmRepository;
import com.smartfarmingassistant.sfa.repository.UserRepository;
import com.smartfarmingassistant.sfa.testutil.PostgresTestContainerBase;
import com.smartfarmingassistant.sfa.service.domain.CropService;
import com.smartfarmingassistant.sfa.service.domain.FarmService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class FarmCropUseCaseIntegrationTest extends PostgresTestContainerBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmService farmService;

    @Autowired
    private CropService cropService;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private CropRepository cropRepository;

    @Test
    void farmCrud_forOwner_worksEndToEnd() {
        User owner = userRepository.save(new User("A", "A", "farmcrud@a.com", "farmcrud", "pw"));

        Long farmId = farmService.create(owner, new FarmCreateRequest("F1", "L1")).id();
        assertThat(farmService.list(owner)).extracting("id").contains(farmId);

        assertThat(farmService.get(owner, farmId).name()).isEqualTo("F1");

        assertThat(farmService.update(owner, farmId, new FarmUpdateRequest("F2", "L2")).name()).isEqualTo("F2");

        farmService.delete(owner, farmId);
        assertThat(farmRepository.findById(farmId)).isEmpty();
        assertThat(farmService.list(owner)).extracting("id").doesNotContain(farmId);
    }

    @Test
    void ownershipIsolation_userCannotAccessOtherUsersFarmOrCrop() {
        User u1 = userRepository.save(new User("A", "A", "own1@a.com", "own1", "pw"));
        User u2 = userRepository.save(new User("B", "B", "own2@b.com", "own2", "pw"));

        Long farmId = farmService.create(u1, new FarmCreateRequest("F", "L")).id();
        Long cropId = cropService.createUnderFarm(
                u1,
                farmId,
                new CropCreateRequest("C", LocalDate.parse("2026-05-01"), SoilType.CLAY)
        ).id();

        assertThatThrownBy(() -> farmService.get(u2, farmId)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> farmService.update(u2, farmId, new FarmUpdateRequest("X", "Y")))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> farmService.delete(u2, farmId)).isInstanceOf(ResourceNotFoundException.class);

        assertThatThrownBy(() -> cropService.get(u2, cropId)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> cropService.update(u2, cropId, new CropUpdateRequest("X", LocalDate.parse("2026-05-02"), SoilType.SANDY)))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> cropService.delete(u2, cropId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cropUseCases_createListUpdateDelete_workForOwner() {
        User owner = userRepository.save(new User("A", "A", "crop@a.com", "cropuser", "pw"));
        Long farmId = farmService.create(owner, new FarmCreateRequest("F", "L")).id();

        Long cropId = cropService.createUnderFarm(
                owner,
                farmId,
                new CropCreateRequest("C1", LocalDate.parse("2026-05-01"), SoilType.CLAY)
        ).id();

        assertThat(cropService.listByFarm(owner, farmId)).extracting("id").contains(cropId);
        assertThat(cropService.get(owner, cropId).name()).isEqualTo("C1");

        assertThat(cropService.update(owner, cropId, new CropUpdateRequest("C2", LocalDate.parse("2026-05-02"), SoilType.SANDY)).name())
                .isEqualTo("C2");

        cropService.delete(owner, cropId);
        assertThat(cropRepository.findById(cropId)).isEmpty();
    }

    @Test
    void deletingFarm_cascadesToCrops() {
        User owner = userRepository.save(new User("A", "A", "cascade@a.com", "cascadeuser", "pw"));
        Long farmId = farmService.create(owner, new FarmCreateRequest("F", "L")).id();

        Long cropId = cropService.createUnderFarm(
                owner,
                farmId,
                new CropCreateRequest("C", LocalDate.parse("2026-05-01"), SoilType.CLAY)
        ).id();

        assertThat(cropRepository.findById(cropId)).isPresent();

        farmService.delete(owner, farmId);

        assertThat(farmRepository.findById(farmId)).isEmpty();
        assertThat(cropRepository.findById(cropId)).isEmpty();
    }
}

