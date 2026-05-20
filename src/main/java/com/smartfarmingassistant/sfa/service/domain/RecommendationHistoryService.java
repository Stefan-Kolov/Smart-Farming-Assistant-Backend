package com.smartfarmingassistant.sfa.service.domain;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.history.RecommendationDto;

public interface RecommendationHistoryService {
    List<RecommendationDto> listForFarm(User user, Long farmId);
    RecommendationDto getById(User user, Long farmId, Long id);
}

