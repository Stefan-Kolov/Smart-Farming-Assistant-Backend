package com.smartfarmingassistant.sfa.service.domain;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmDto;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmUpdateRequest;

public interface FarmService {
    List<FarmDto> list(User user);

    FarmDto create(User user, FarmCreateRequest request);

    FarmDto get(User user, Long farmId);

    FarmDto update(User user, Long farmId, FarmUpdateRequest request);

    void delete(User user, Long farmId);
}

