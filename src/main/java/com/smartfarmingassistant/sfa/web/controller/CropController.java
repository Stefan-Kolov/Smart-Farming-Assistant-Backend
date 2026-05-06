package com.smartfarmingassistant.sfa.web.controller;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.crop.CropCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.crop.CropDto;
import com.smartfarmingassistant.sfa.model.dto.crop.CropUpdateRequest;
import com.smartfarmingassistant.sfa.service.domain.CropService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CropController {
    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping("/farms/{farmId}/crops")
    public List<CropDto> listByFarm(@AuthenticationPrincipal User user, @PathVariable Long farmId) {
        return cropService.listByFarm(user, farmId);
    }

    @PostMapping("/farms/{farmId}/crops")
    @ResponseStatus(HttpStatus.CREATED)
    public CropDto createUnderFarm(
            @AuthenticationPrincipal User user,
            @PathVariable Long farmId,
            @Valid @RequestBody CropCreateRequest request
    ) {
        return cropService.createUnderFarm(user, farmId, request);
    }

    @GetMapping("/crops/{cropId}")
    public CropDto get(@AuthenticationPrincipal User user, @PathVariable Long cropId) {
        return cropService.get(user, cropId);
    }

    @PutMapping("/crops/{cropId}")
    public CropDto update(
            @AuthenticationPrincipal User user,
            @PathVariable Long cropId,
            @Valid @RequestBody CropUpdateRequest request
    ) {
        return cropService.update(user, cropId, request);
    }

    @DeleteMapping("/crops/{cropId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable Long cropId) {
        cropService.delete(user, cropId);
    }
}

