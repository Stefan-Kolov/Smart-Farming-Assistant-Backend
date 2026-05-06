package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.domain.entity.Recommendation;
import com.smartfarmingassistant.sfa.model.dto.history.RecommendationDto;
import com.smartfarmingassistant.sfa.model.exception.ResourceNotFoundException;
import com.smartfarmingassistant.sfa.repository.FarmRepository;
import com.smartfarmingassistant.sfa.repository.RecommendationRepository;
import com.smartfarmingassistant.sfa.service.domain.RecommendationHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RecommendationHistoryServiceImpl implements RecommendationHistoryService {
    private final RecommendationRepository recommendationRepository;
    private final FarmRepository farmRepository;

    public RecommendationHistoryServiceImpl(
            RecommendationRepository recommendationRepository,
            FarmRepository farmRepository
    ) {
        this.recommendationRepository = recommendationRepository;
        this.farmRepository = farmRepository;
    }

    @Override
    public List<RecommendationDto> listForFarm(User user, Long farmId) {
        if (farmRepository.findByIdAndUser(farmId, user).isEmpty()) {
            throw new ResourceNotFoundException("Farm not found");
        }

        return recommendationRepository.findAllByFarmIdAndFarmUserOrderByCreatedAtDesc(farmId, user).stream()
                .map(this::toDto)
                .toList();
    }

    private RecommendationDto toDto(Recommendation recommendation) {
        return new RecommendationDto(
                recommendation.getId(),
                recommendation.getContent(),
                recommendation.getRiskLevel(),
                recommendation.getTemperature(),
                recommendation.getHumidity(),
                recommendation.getRainfall(),
                recommendation.getCreatedAt(),
                recommendation.getFarm().getId(),
                recommendation.getCrop() == null ? null : recommendation.getCrop().getId()
        );
    }
}

