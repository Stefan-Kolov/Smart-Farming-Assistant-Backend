package com.smartfarmingassistant.sfa.service.domain;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.history.WeatherRecordDto;

public interface WeatherRecordHistoryService {
    List<WeatherRecordDto> listForFarm(User user, Long farmId);
}

