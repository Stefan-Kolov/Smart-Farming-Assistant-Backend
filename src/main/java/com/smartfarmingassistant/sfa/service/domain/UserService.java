package com.smartfarmingassistant.sfa.service.domain;


import java.util.Optional;
import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.UpdateProfileRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Optional<User> findByUsername(String username);

    User register(User user);

    User login(String username, String password);

    User updateProfile(String username, UpdateProfileRequestDto request);
}

