package com.smartfarmingassistant.sfa.web.controller;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmDto;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmUpdateRequest;
import com.smartfarmingassistant.sfa.service.domain.FarmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping
    public List<FarmDto> list(@AuthenticationPrincipal User user) {
        return farmService.list(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FarmDto create(@AuthenticationPrincipal User user, @Valid @RequestBody FarmCreateRequest request) {
        return farmService.create(user, request);
    }

    @GetMapping("/{farmId}")
    public FarmDto get(@AuthenticationPrincipal User user, @PathVariable Long farmId) {
        return farmService.get(user, farmId);
    }

    @PutMapping("/{farmId}")
    public FarmDto update(
            @AuthenticationPrincipal User user,
            @PathVariable Long farmId,
            @Valid @RequestBody FarmUpdateRequest request
    ) {
        return farmService.update(user, farmId, request);
    }

    @DeleteMapping("/{farmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable Long farmId) {
        farmService.delete(user, farmId);
    }
}

