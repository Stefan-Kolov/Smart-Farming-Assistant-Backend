package com.smartfarmingassistant.sfa.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.smartfarmingassistant.sfa.model.dto.weather.CurrentWeatherResponseDto;
import com.smartfarmingassistant.sfa.model.dto.weather.GeocodingResultDto;
import com.smartfarmingassistant.sfa.model.exception.GlobalExceptionHandler;
import com.smartfarmingassistant.sfa.service.domain.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class WeatherControllerTest {

    private WeatherService weatherService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        weatherService = Mockito.mock(WeatherService.class);
        WeatherController controller = new WeatherController(weatherService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void geocode_returnsResults() throws Exception {
        when(weatherService.geocode(eq("Skopje"), any(), any()))
                .thenReturn(List.of(new GeocodingResultDto(
                        "Skopje", "North Macedonia", "MK", "Skopje", null, 41.99, 21.43, "Europe/Skopje"
                )));

        mockMvc.perform(get("/api/weather/geocode").param("name", "Skopje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Skopje"))
                .andExpect(jsonPath("$[0].latitude").value(41.99));
    }

    @Test
    void current_byCoordinates_callsService() throws Exception {
        CurrentWeatherResponseDto dto = new CurrentWeatherResponseDto(
                41.99,
                21.43,
                41.99,
                21.43,
                "Europe/Skopje",
                "2026-04-11T12:00",
                900,
                18.5,
                55.0,
                0.0,
                5.0,
                9.2,
                1,
                1,
                0.2,
                16.1,
                15.8,
                0.21,
                0.23,
                Map.of("temperature_2m", "°C")
        );
        when(weatherService.currentByCoordinates(41.99, 21.43)).thenReturn(dto);

        mockMvc.perform(get("/api/weather/current")
                        .param("lat", "41.99")
                        .param("lon", "21.43"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(41.99))
                .andExpect(jsonPath("$.temperature2m").value(18.5));

        verify(weatherService).currentByCoordinates(41.99, 21.43);
    }

    @Test
    void current_requiresLatAndLonTogether() throws Exception {
        mockMvc.perform(get("/api/weather/current").param("lat", "41.99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void current_byLocationName_callsService() throws Exception {
        CurrentWeatherResponseDto dto = new CurrentWeatherResponseDto(
                null,
                null,
                41.99,
                21.43,
                "Europe/Skopje",
                "2026-04-11T12:00",
                900,
                18.5,
                55.0,
                0.0,
                5.0,
                9.2,
                1,
                1,
                0.2,
                16.1,
                15.8,
                0.21,
                0.23,
                Map.of()
        );
        when(weatherService.currentByLocationName("Skopje", Optional.empty())).thenReturn(dto);

        mockMvc.perform(get("/api/weather/current").param("locationName", "Skopje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(41.99));

        verify(weatherService).currentByLocationName("Skopje", Optional.empty());
    }
}
