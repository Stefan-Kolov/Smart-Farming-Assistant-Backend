package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.domain.entity.WeatherRecord;
import com.smartfarmingassistant.sfa.model.dto.history.WeatherRecordDto;
import com.smartfarmingassistant.sfa.model.exception.ResourceNotFoundException;
import com.smartfarmingassistant.sfa.repository.FarmRepository;
import com.smartfarmingassistant.sfa.repository.WeatherRecordRepository;
import com.smartfarmingassistant.sfa.service.domain.WeatherRecordHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WeatherRecordHistoryServiceImpl implements WeatherRecordHistoryService {
    private final WeatherRecordRepository weatherRecordRepository;
    private final FarmRepository farmRepository;

    public WeatherRecordHistoryServiceImpl(
            WeatherRecordRepository weatherRecordRepository,
            FarmRepository farmRepository
    ) {
        this.weatherRecordRepository = weatherRecordRepository;
        this.farmRepository = farmRepository;
    }

    @Override
    public List<WeatherRecordDto> listForFarm(User user, Long farmId) {
        if (farmRepository.findByIdAndUser(farmId, user).isEmpty()) {
            throw new ResourceNotFoundException("Farm not found");
        }

        return weatherRecordRepository.findAllByFarmIdAndFarmUserOrderByRecordedAtDesc(farmId, user).stream()
                .map(this::toDto)
                .toList();
    }

    private WeatherRecordDto toDto(WeatherRecord weatherRecord) {
        return new WeatherRecordDto(
                weatherRecord.getId(),
                weatherRecord.getTemperature(),
                weatherRecord.getHumidity(),
                weatherRecord.getRainfall(),
                weatherRecord.getRecordedAt(),
                weatherRecord.getFarm().getId()
        );
    }
}

