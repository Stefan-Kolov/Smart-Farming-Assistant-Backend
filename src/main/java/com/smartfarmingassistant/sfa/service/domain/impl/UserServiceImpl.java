package com.smartfarmingassistant.sfa.service.domain.impl;


import java.util.Optional;
import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.UpdateProfileRequestDto;
import com.smartfarmingassistant.sfa.model.exception.EmailAlreadyExistsException;
import com.smartfarmingassistant.sfa.model.exception.IncorrectPasswordException;
import com.smartfarmingassistant.sfa.model.exception.UserNotFoundException;
import com.smartfarmingassistant.sfa.model.exception.UsernameAlreadyExistsException;
import com.smartfarmingassistant.sfa.repository.UserRepository;
import com.smartfarmingassistant.sfa.service.domain.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UsernameAlreadyExistsException(user.getUsername());
        return userRepository.save(new User(
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getUsername(),
                passwordEncoder.encode(user.getPassword())
        ));
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new IncorrectPasswordException();
        return user;
    }

    @Override
    @Transactional
    public User updateProfile(String username, UpdateProfileRequestDto request) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (userRepository.existsByEmailAndUsernameNot(request.email(), username)) {
            throw new EmailAlreadyExistsException(request.email());
        }
        if (userRepository.existsByUsernameAndUsernameNot(request.username(), username)) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        user.setUsername(request.username());
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
