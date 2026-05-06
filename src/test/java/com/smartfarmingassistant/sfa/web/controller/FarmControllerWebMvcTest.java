package com.smartfarmingassistant.sfa.web.controller;

import java.util.List;

import com.smartfarmingassistant.sfa.model.dto.farm.FarmCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmDto;
import com.smartfarmingassistant.sfa.model.dto.farm.FarmUpdateRequest;
import com.smartfarmingassistant.sfa.service.domain.FarmService;
import com.smartfarmingassistant.sfa.web.filter.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FarmController.class,
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
class FarmControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FarmService farmService;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void list_returnsDtos() throws Exception {
        when(farmService.list(any())).thenReturn(List.of(new FarmDto(1L, "Farm", "Loc")));

        mockMvc.perform(get("/api/farms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Farm"))
                .andExpect(jsonPath("$[0].location").value("Loc"));
    }

    @Test
    void create_validRequest_returnsCreated() throws Exception {
        when(farmService.create(any(), any(FarmCreateRequest.class)))
                .thenReturn(new FarmDto(1L, "Farm", "Loc"));

        mockMvc.perform(post("/api/farms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Farm\",\"location\":\"Loc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/farms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"location\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_returnsDto() throws Exception {
        when(farmService.get(any(), eq(5L))).thenReturn(new FarmDto(5L, "F", "L"));

        mockMvc.perform(get("/api/farms/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void update_validRequest_returnsDto() throws Exception {
        when(farmService.update(any(), eq(2L), any(FarmUpdateRequest.class)))
                .thenReturn(new FarmDto(2L, "F2", "L2"));

        mockMvc.perform(put("/api/farms/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"F2\",\"location\":\"L2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(farmService).delete(any(), eq(9L));

        mockMvc.perform(delete("/api/farms/9"))
                .andExpect(status().isNoContent());
    }
}
