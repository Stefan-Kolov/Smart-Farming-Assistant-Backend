package com.smartfarmingassistant.sfa.security;

import com.smartfarmingassistant.sfa.helpers.JwtHelper;
import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.repository.UserRepository;
import com.smartfarmingassistant.sfa.service.domain.FarmService;
import com.smartfarmingassistant.sfa.testutil.PostgresTestContainerBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtHttpIntegrationTest extends PostgresTestContainerBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private FarmService farmService;

    @Test
    void farmsEndpoint_requiresAuth_missingOrInvalidToken_isDenied() throws Exception {
        mockMvc.perform(get("/api/farms"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));

        mockMvc.perform(get("/api/farms").header("Authorization", "Bearer invalid.token.value"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(401, 403));
    }

    @Test
    void validToken_allowsAccess_andOwnershipIsEnforcedWith404() throws Exception {
        User userA = userRepository.save(new User("A", "A", "jwtA@a.com", "jwtA", "pw"));
        User userB = userRepository.save(new User("B", "B", "jwtB@b.com", "jwtB", "pw"));

        Long farmId = farmService.create(userA, new FarmCreateRequest("F", "L")).id();

        String tokenA = jwtHelper.generateToken(userA);
        String tokenB = jwtHelper.generateToken(userB);

        mockMvc.perform(get("/api/farms").header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/farms/" + farmId).header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());
    }
}

