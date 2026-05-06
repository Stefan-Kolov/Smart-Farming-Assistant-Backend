package com.smartfarmingassistant.sfa.repository;

import java.time.LocalDate;
import java.util.List;

import com.smartfarmingassistant.sfa.config.TestJpaAuditingConfig;
import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.domain.entity.Crop;
import com.smartfarmingassistant.sfa.model.domain.entity.Farm;
import com.smartfarmingassistant.sfa.model.domain.entity.Recommendation;
import com.smartfarmingassistant.sfa.model.domain.entity.WeatherRecord;
import com.smartfarmingassistant.sfa.model.enums.RiskLevel;
import com.smartfarmingassistant.sfa.model.enums.SoilType;
import com.smartfarmingassistant.sfa.testutil.PostgresTestContainerBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("integration")
class ScopedRepositoriesDataJpaTest extends PostgresTestContainerBase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private WeatherRecordRepository weatherRecordRepository;

    @Test
    void farmRepository_scopesByUser() {
        User u1 = userRepository.save(new User("A", "A", "a@a.com", "u1", "pw"));
        User u2 = userRepository.save(new User("B", "B", "b@b.com", "u2", "pw"));

        Farm f1 = farmRepository.save(new Farm("F1", "L1", u1));
        farmRepository.save(new Farm("F2", "L2", u2));

        List<Farm> u1Farms = farmRepository.findAllByUser(u1);
        assertThat(u1Farms).hasSize(1);
        assertThat(u1Farms.get(0).getId()).isEqualTo(f1.getId());

        assertThat(farmRepository.findByIdAndUser(f1.getId(), u2)).isEmpty();
    }

    @Test
    void cropRepository_scopesByFarmAndUser() {
        User u1 = userRepository.save(new User("A", "A", "a2@a.com", "u11", "pw"));
        User u2 = userRepository.save(new User("B", "B", "b2@b.com", "u22", "pw"));

        Farm f1 = farmRepository.save(new Farm("F1", "L1", u1));
        Farm f2 = farmRepository.save(new Farm("F2", "L2", u2));

        Crop c1 = cropRepository.save(new Crop("C1", LocalDate.parse("2026-05-01"), SoilType.CLAY, f1));
        cropRepository.save(new Crop("C2", LocalDate.parse("2026-05-01"), SoilType.SANDY, f2));

        assertThat(cropRepository.findAllByFarmIdAndFarmUser(f1.getId(), u1))
                .extracting(Crop::getId)
                .containsExactly(c1.getId());

        assertThat(cropRepository.findByIdAndFarmUser(c1.getId(), u2)).isEmpty();
    }

    @Test
    void historyRepositories_scopeByFarmAndUser_andOrderDesc() {
        User u1 = userRepository.save(new User("A", "A", "a3@a.com", "u111", "pw"));
        Farm f1 = farmRepository.save(new Farm("F1", "L1", u1));

        Recommendation r1 = new Recommendation("old", RiskLevel.LOW, 1.0, 1.0, 1.0, f1, null);
        recommendationRepository.save(r1);

        Recommendation r2 = new Recommendation("new", RiskLevel.HIGH, 2.0, 2.0, 2.0, f1, null);
        recommendationRepository.save(r2);

        List<Recommendation> recs = recommendationRepository.findAllByFarmIdAndFarmUserOrderByCreatedAtDesc(f1.getId(), u1);
        assertThat(recs).hasSize(2);
        assertThat(recs.get(0).getCreatedAt()).isAfterOrEqualTo(recs.get(1).getCreatedAt());

        WeatherRecord w1 = new WeatherRecord(1.0, 1.0, 1.0, f1);
        weatherRecordRepository.save(w1);
        WeatherRecord w2 = new WeatherRecord(2.0, 2.0, 2.0, f1);
        weatherRecordRepository.save(w2);

        List<WeatherRecord> weather = weatherRecordRepository.findAllByFarmIdAndFarmUserOrderByRecordedAtDesc(f1.getId(), u1);
        assertThat(weather).hasSize(2);
        assertThat(weather.get(0).getRecordedAt()).isAfterOrEqualTo(weather.get(1).getRecordedAt());
    }
}
