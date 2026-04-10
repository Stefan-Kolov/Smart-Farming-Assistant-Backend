package com.smartfarmingassistant.sfa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SmartFarmingAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFarmingAssistantApplication.class, args);
    }

}
