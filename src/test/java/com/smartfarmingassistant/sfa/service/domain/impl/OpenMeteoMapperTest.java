package com.smartfarmingassistant.sfa.service.domain.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfarmingassistant.sfa.model.dto.weather.CurrentWeatherResponseDto;
import org.junit.jupiter.api.Test;

class OpenMeteoMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void toCurrentWeatherResponse_mapsExpectedFields() throws Exception {
        String json = """
                {
                  "latitude": 41.99,
                  "longitude": 21.43,
                  "timezone": "Europe/Skopje",
                  "current_units": {
                    "temperature_2m": "°C",
                    "relative_humidity_2m": "%",
                    "precipitation": "mm",
                    "wind_speed_10m": "km/h"
                  },
                  "current": {
                    "time": "2026-04-11T12:00",
                    "interval": 900,
                    "temperature_2m": 18.5,
                    "relative_humidity_2m": 55,
                    "precipitation": 0.0,
                    "precipitation_probability": 5,
                    "wind_speed_10m": 9.2,
                    "weather_code": 1,
                    "is_day": 1,
                    "et0_fao_evapotranspiration": 0.2,
                    "soil_temperature_0cm": 16.1,
                    "soil_temperature_6cm": 15.8,
                    "soil_moisture_0_to_1cm": 0.21,
                    "soil_moisture_1_to_3cm": 0.23
                  }
                }
                """;

        JsonNode root = objectMapper.readTree(json);
        CurrentWeatherResponseDto dto = OpenMeteoMapper.toCurrentWeatherResponse(root, 41.99, 21.43);

        assertEquals(41.99, dto.requestedLatitude());
        assertEquals(21.43, dto.requestedLongitude());
        assertEquals(41.99, dto.latitude());
        assertEquals(21.43, dto.longitude());
        assertEquals("Europe/Skopje", dto.timezone());
        assertEquals("2026-04-11T12:00", dto.time());
        assertEquals(900, dto.intervalSeconds());
        assertEquals(18.5, dto.temperature2m());
        assertEquals(55.0, dto.relativeHumidity2m());
        assertEquals(0.0, dto.precipitation());
        assertEquals(5.0, dto.precipitationProbability());
        assertEquals(9.2, dto.windSpeed10m());
        assertEquals(1, dto.weatherCode());
        assertEquals(1, dto.isDay());
        assertEquals(0.2, dto.et0FaoEvapotranspiration());
        assertEquals(16.1, dto.soilTemperature0cm());
        assertEquals(15.8, dto.soilTemperature6cm());
        assertEquals(0.21, dto.soilMoisture0To1cm());
        assertEquals(0.23, dto.soilMoisture1To3cm());

        assertNotNull(dto.units());
        assertEquals("°C", dto.units().get("temperature_2m"));
        assertEquals("%", dto.units().get("relative_humidity_2m"));
    }
}

