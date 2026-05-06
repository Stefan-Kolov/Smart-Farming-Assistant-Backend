package com.smartfarmingassistant.sfa.web.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.smartfarmingassistant.sfa.model.dto.history.RecommendationDto;
import com.smartfarmingassistant.sfa.model.dto.history.WeatherRecordDto;
import com.smartfarmingassistant.sfa.model.enums.RiskLevel;
import com.smartfarmingassistant.sfa.service.domain.RecommendationHistoryService;
import com.smartfarmingassistant.sfa.service.domain.WeatherRecordHistoryService;
import com.smartfarmingassistant.sfa.web.filter.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FarmHistoryController.class,
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
class FarmHistoryControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationHistoryService recommendationHistoryService;

    @MockBean
    private WeatherRecordHistoryService weatherRecordHistoryService;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void recommendations_returnsDtos() throws Exception {
        when(recommendationHistoryService.listForFarm(any(), eq(1L)))
                .thenReturn(List.of(new RecommendationDto(
                        1L,
                        "content",
                        RiskLevel.LOW,
                        1.0,
                        2.0,
                        3.0,
                        LocalDateTime.parse("2026-05-03T10:15:30"),
                        1L,
                        null
                )));

        mockMvc.perform(get("/api/farms/1/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void weatherRecords_returnsDtos() throws Exception {
        when(weatherRecordHistoryService.listForFarm(any(), eq(1L)))
                .thenReturn(List.of(new WeatherRecordDto(
                        1L,
                        1.0,
                        2.0,
                        3.0,
                        LocalDateTime.parse("2026-05-03T10:15:30"),
                        1L
                )));

        mockMvc.perform(get("/api/farms/1/weather-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
