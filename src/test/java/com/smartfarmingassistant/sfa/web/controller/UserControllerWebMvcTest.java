package com.smartfarmingassistant.sfa.web.controller;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.RegisterUserResponseDto;
import com.smartfarmingassistant.sfa.model.dto.UpdateProfileRequestDto;
import com.smartfarmingassistant.sfa.model.dto.UpdateProfileResponseDto;
import com.smartfarmingassistant.sfa.service.application.UserApplicationService;
import com.smartfarmingassistant.sfa.web.filter.JwtFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.smartfarmingassistant.sfa.config.JpaAuditingConfig.class
        ),
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userApplicationService;

    @MockBean
    private JwtFilter jwtFilter;

    @BeforeEach
    void setAuthentication() {
        User user = new User("Old", "Surname", "old@example.com", "user1", "password");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateProfile_validRequest_returnsSuccessMessageAndUser() throws Exception {
        RegisterUserResponseDto user = new RegisterUserResponseDto(
                "user2",
                "New",
                "NewSurname",
                "new@example.com",
                com.smartfarmingassistant.sfa.model.enums.Role.ROLE_USER
        );
        when(userApplicationService.updateProfile(eq("user1"), any(UpdateProfileRequestDto.class)))
                .thenReturn(java.util.Optional.of(UpdateProfileResponseDto.from(user, "new-token")));

        mockMvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user2\",\"name\":\"New\",\"surname\":\"NewSurname\",\"email\":\"new@example.com\",\"password\":\"new-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully."))
                .andExpect(jsonPath("$.user.username").value("user2"))
                .andExpect(jsonPath("$.user.name").value("New"))
                .andExpect(jsonPath("$.user.surname").value("NewSurname"))
                .andExpect(jsonPath("$.user.email").value("new@example.com"))
                .andExpect(jsonPath("$.token").value("new-token"));
    }

    @Test
    void updateProfile_invalidRequest_returns400() throws Exception {
        mockMvc.perform(put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"name\":\"\",\"surname\":\"\",\"email\":\"not-email\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }
}
