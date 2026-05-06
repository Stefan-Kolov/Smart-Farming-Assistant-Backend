package com.smartfarmingassistant.sfa.web.controller;

import java.time.LocalDate;
import java.util.List;

import com.smartfarmingassistant.sfa.model.dto.crop.CropCreateRequest;
import com.smartfarmingassistant.sfa.model.dto.crop.CropDto;
import com.smartfarmingassistant.sfa.model.dto.crop.CropUpdateRequest;
import com.smartfarmingassistant.sfa.model.enums.SoilType;
import com.smartfarmingassistant.sfa.service.domain.CropService;
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
        controllers = CropController.class,
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
class CropControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CropService cropService;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void listByFarm_returnsDtos() throws Exception {
        when(cropService.listByFarm(any(), eq(1L)))
                .thenReturn(List.of(new CropDto(10L, "Crop", LocalDate.parse("2026-05-01"), SoilType.CLAY, 1L)));

        mockMvc.perform(get("/api/farms/1/crops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].farmId").value(1));
    }

    @Test
    void createUnderFarm_valid_returnsCreated() throws Exception {
        when(cropService.createUnderFarm(any(), eq(1L), any(CropCreateRequest.class)))
                .thenReturn(new CropDto(10L, "Crop", LocalDate.parse("2026-05-01"), SoilType.CLAY, 1L));

        mockMvc.perform(post("/api/farms/1/crops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Crop\",\"plantingDate\":\"2026-05-01\",\"soilType\":\"CLAY\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void createUnderFarm_invalid_returns400() throws Exception {
        mockMvc.perform(post("/api/farms/1/crops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"plantingDate\":null,\"soilType\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_returnsDto() throws Exception {
        when(cropService.get(any(), eq(5L)))
                .thenReturn(new CropDto(5L, "C", LocalDate.parse("2026-05-01"), SoilType.SANDY, 1L));

        mockMvc.perform(get("/api/crops/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void update_valid_returnsDto() throws Exception {
        when(cropService.update(any(), eq(5L), any(CropUpdateRequest.class)))
                .thenReturn(new CropDto(5L, "C2", LocalDate.parse("2026-05-02"), SoilType.LOAMY, 1L));

        mockMvc.perform(put("/api/crops/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"C2\",\"plantingDate\":\"2026-05-02\",\"soilType\":\"LOAMY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("C2"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        doNothing().when(cropService).delete(any(), eq(5L));

        mockMvc.perform(delete("/api/crops/5"))
                .andExpect(status().isNoContent());
    }
}
