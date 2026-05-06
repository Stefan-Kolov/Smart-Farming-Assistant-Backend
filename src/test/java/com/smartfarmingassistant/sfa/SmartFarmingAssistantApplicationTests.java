package com.smartfarmingassistant.sfa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.smartfarmingassistant.sfa.testutil.PostgresTestContainerBase;
import org.junit.jupiter.api.Tag;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
class SmartFarmingAssistantApplicationTests extends PostgresTestContainerBase {

    @Test
    void contextLoads() {
    }

}
