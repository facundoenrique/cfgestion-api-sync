package org.api_sync.config;

import org.api_sync.services.afip.config.AfipServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AfipConfig {

    @Bean
    public AfipServiceConfig afipServiceConfig() {
        return AfipServiceConfig.getDefaultConfig();
    }
} 