package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.domain.entity.Farm;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmDto;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmUpdateRequest;
import com.smartfarmingassistant.sfa.model.exception.ResourceNotFoundException;
import com.smartfarmingassistant.sfa.repository.FarmRepository;
import com.smartfarmingassistant.sfa.service.domain.FarmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FarmServiceImpl implements FarmService {
    private final FarmRepository farmRepository;

    public FarmServiceImpl(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmDto> list(User user) {
        return farmRepository.findAllByUser(user).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public FarmDto create(User user, FarmCreateRequest request) {
        Farm farm = new Farm(request.name(), request.location(), user);
        return toDto(farmRepository.save(farm));
    }

    @Override
    @Transactional(readOnly = true)
    public FarmDto get(User user, Long farmId) {
        return toDto(findOwnedFarmOrThrow(user, farmId));
    }

    @Override
    public FarmDto update(User user, Long farmId, FarmUpdateRequest request) {
        Farm farm = findOwnedFarmOrThrow(user, farmId);
        farm.setName(request.name());
        farm.setLocation(request.location());
        return toDto(farm);
    }

    @Override
    public void delete(User user, Long farmId) {
        Farm farm = findOwnedFarmOrThrow(user, farmId);
        farmRepository.delete(farm);
    }

    private Farm findOwnedFarmOrThrow(User user, Long farmId) {
        return farmRepository.findByIdAndUser(farmId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));
    }

    private FarmDto toDto(Farm farm) {
        return new FarmDto(farm.getId(), farm.getName(), farm.getLocation());
    }
}

