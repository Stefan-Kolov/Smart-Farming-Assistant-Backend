package com.smartfarmingassistant.sfa.service.domain;


import java.util.Optional;
import com.smartfarmingassistant.sfa.model.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    Optional<User> findByUsername(String username);

    User register(User user);

    User login(String username, String password);
}

