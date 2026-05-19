package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.Optional;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.UpdateProfileRequestDto;
import com.smartfarmingassistant.sfa.model.exception.EmailAlreadyExistsException;
import com.smartfarmingassistant.sfa.model.exception.UsernameAlreadyExistsException;
import com.smartfarmingassistant.sfa.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateProfile_updatesUsernameNameSurnameEmailAndEncodedPassword() {
        User user = new User("Old", "Surname", "old@example.com", "user1", "old-password");
        UpdateProfileRequestDto request = new UpdateProfileRequestDto("user2", "New", "NewSurname", "new@example.com", "new-password");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndUsernameNot("new@example.com", "user1")).thenReturn(false);
        when(userRepository.existsByUsernameAndUsernameNot("user2", "user1")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateProfile("user1", request);

        assertThat(updated.getUsername()).isEqualTo("user2");
        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getSurname()).isEqualTo("NewSurname");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getPassword()).isEqualTo("encoded-password");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_keepsExistingPasswordWhenPasswordIsNull() {
        User user = new User("Old", "Surname", "old@example.com", "user1", "old-password");
        UpdateProfileRequestDto request = new UpdateProfileRequestDto("user1", "New", "Surname", "new@example.com", null);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndUsernameNot("new@example.com", "user1")).thenReturn(false);
        when(userRepository.existsByUsernameAndUsernameNot("user1", "user1")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateProfile("user1", request);

        assertThat(updated.getPassword()).isEqualTo("old-password");
        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void updateProfile_duplicateEmail_throwsConflictException() {
        User user = new User("Old", "Surname", "old@example.com", "user1", "old-password");
        UpdateProfileRequestDto request = new UpdateProfileRequestDto("user1", "New", "Surname", "taken@example.com", null);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndUsernameNot("taken@example.com", "user1")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile("user1", request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("taken@example.com");

        verify(userRepository, never()).save(user);
    }

    @Test
    void updateProfile_duplicateUsername_throwsConflictException() {
        User user = new User("Old", "Surname", "old@example.com", "user1", "old-password");
        UpdateProfileRequestDto request = new UpdateProfileRequestDto("taken", "New", "Surname", "new@example.com", null);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndUsernameNot("new@example.com", "user1")).thenReturn(false);
        when(userRepository.existsByUsernameAndUsernameNot("taken", "user1")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile("user1", request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("taken");

        verify(userRepository, never()).save(user);
    }
}
