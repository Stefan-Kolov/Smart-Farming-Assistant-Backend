package com.smartfarmingassistant.sfa.service.application;


import java.util.Optional;
import com.smartfarmingassistant.sfa.model.dto.LoginUserRequestDto;
import com.smartfarmingassistant.sfa.model.dto.LoginUserResponseDto;
import com.smartfarmingassistant.sfa.model.dto.RegisterUserRequestDto;
import com.smartfarmingassistant.sfa.model.dto.RegisterUserResponseDto;

public interface UserApplicationService {
    Optional<RegisterUserResponseDto> register(RegisterUserRequestDto registerUserRequestDto);

    Optional<LoginUserResponseDto> login(LoginUserRequestDto loginUserRequestDto);

    Optional<RegisterUserResponseDto> findByUsername(String username);
}

