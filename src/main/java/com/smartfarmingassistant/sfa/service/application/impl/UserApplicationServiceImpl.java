package com.smartfarmingassistant.sfa.service.application.impl;


import java.util.Optional;
import com.smartfarmingassistant.sfa.helpers.JwtHelper;
import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.LoginUserRequestDto;
import com.smartfarmingassistant.sfa.model.dto.LoginUserResponseDto;
import com.smartfarmingassistant.sfa.model.dto.RegisterUserRequestDto;
import com.smartfarmingassistant.sfa.model.dto.RegisterUserResponseDto;
import com.smartfarmingassistant.sfa.service.application.UserApplicationService;
import com.smartfarmingassistant.sfa.service.domain.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserService userService;
    private final JwtHelper jwtHelper;

    public UserApplicationServiceImpl(UserService userService, JwtHelper jwtHelper) {
        this.userService = userService;
        this.jwtHelper = jwtHelper;
    }

    @Override
    public Optional<RegisterUserResponseDto> register(RegisterUserRequestDto registerUserRequestDto) {
        User user = userService.register(registerUserRequestDto.toUser());
        RegisterUserResponseDto displayUserDto = RegisterUserResponseDto.from(user);
        return Optional.of(displayUserDto);
    }

    @Override
    public Optional<LoginUserResponseDto> login(LoginUserRequestDto loginUserRequestDto) {
        User user = userService.login(loginUserRequestDto.username(), loginUserRequestDto.password());

        String token = jwtHelper.generateToken(user);

        return Optional.of(new LoginUserResponseDto(token));
    }

    @Override
    public Optional<RegisterUserResponseDto> findByUsername(String username) {
        return userService
                .findByUsername(username)
                .map(RegisterUserResponseDto::from);
    }
}


