package com.smartfarmingassistant.sfa.service.domain;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.crop.CropCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.crop.CropDto;
import com.smartfarmingassistant.sfa.model.dto.crop.CropUpdateRequest;

public interface CropService {
    List<CropDto> listByFarm(User user, Long farmId);

    CropDto createUnderFarm(User user, Long farmId, CropCreateRequest request);

    CropDto get(User user, Long cropId);

    CropDto update(User user, Long cropId, CropUpdateRequest request);

    void delete(User user, Long cropId);
}

