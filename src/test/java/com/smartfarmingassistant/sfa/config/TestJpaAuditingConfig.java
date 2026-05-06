package com.smartfarmingassistant.sfa.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaAuditingConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("test");
    }
}
