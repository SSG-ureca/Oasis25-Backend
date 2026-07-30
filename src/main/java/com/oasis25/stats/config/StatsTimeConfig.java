package com.oasis25.stats.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsTimeConfig {

    @Value("${oasis.stats.zone-id:Asia/Seoul}")
    private String zoneIdValue;

    @Bean
    public ZoneId statsZoneId() {
        return ZoneId.of(zoneIdValue);
    }

    @Bean
    public Clock statsClock(ZoneId statsZoneId) {
        return Clock.system(statsZoneId);
    }
}
