package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.domain.entity.Crop;
import com.smartfarmingassistant.sfa.model.domain.entity.Farm;
import com.smartfarmingassistant.sfa.model.dto.crop.CropCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.crop.CropDto;
import com.smartfarmingassistant.sfa.model.dto.crop.CropUpdateRequest;
import com.smartfarmingassistant.sfa.model.exception.ResourceNotFoundException;
import com.smartfarmingassistant.sfa.repository.CropRepository;
import com.smartfarmingassistant.sfa.repository.FarmRepository;
import com.smartfarmingassistant.sfa.service.domain.CropService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CropServiceImpl implements CropService {
    private final CropRepository cropRepository;
    private final FarmRepository farmRepository;

    public CropServiceImpl(CropRepository cropRepository, FarmRepository farmRepository) {
        this.cropRepository = cropRepository;
        this.farmRepository = farmRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropDto> listByFarm(User user, Long farmId) {
        if (farmRepository.findByIdAndUser(farmId, user).isEmpty()) {
            throw new ResourceNotFoundException("Farm not found");
        }
        return cropRepository.findAllByFarmIdAndFarmUser(farmId, user).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public CropDto createUnderFarm(User user, Long farmId, CropCreateRequest request) {
        Farm farm = farmRepository.findByIdAndUser(farmId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found"));

        Crop crop = new Crop(request.name(), request.plantingDate(), request.soilType(), farm);
        return toDto(cropRepository.save(crop));
    }

    @Override
    @Transactional(readOnly = true)
    public CropDto get(User user, Long cropId) {
        return toDto(findOwnedCropOrThrow(user, cropId));
    }

    @Override
    public CropDto update(User user, Long cropId, CropUpdateRequest request) {
        Crop crop = findOwnedCropOrThrow(user, cropId);
        crop.setName(request.name());
        crop.setPlantingDate(request.plantingDate());
        crop.setSoilType(request.soilType());
        return toDto(crop);
    }

    @Override
    public void delete(User user, Long cropId) {
        Crop crop = findOwnedCropOrThrow(user, cropId);
        cropRepository.delete(crop);
    }

    private Crop findOwnedCropOrThrow(User user, Long cropId) {
        return cropRepository.findByIdAndFarmUser(cropId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found"));
    }

    private CropDto toDto(Crop crop) {
        return new CropDto(
                crop.getId(),
                crop.getName(),
                crop.getPlantingDate(),
                crop.getSoilType(),
                crop.getFarm().getId()
        );
    }
}

