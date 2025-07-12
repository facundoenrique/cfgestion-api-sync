package org.api_sync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Configuración para manejar la zona horaria de Argentina
 * Argentina usa UTC-3 (o UTC-2 durante horario de verano)
 */
@Configuration
@EnableScheduling
public class TimeZoneConfig {

    /**
     * Configura la zona horaria por defecto para Argentina
     * Esto afecta a todos los cron jobs y operaciones de fecha/hora
     */
    @Bean
    public TimeZone argentinaTimeZone() {
        // Argentina/America/Argentina/Buenos_Aires maneja automáticamente el horario de verano
        TimeZone argentinaTz = TimeZone.getTimeZone("America/Argentina/Buenos_Aires");
        TimeZone.setDefault(argentinaTz);
        return argentinaTz;
    }

    /**
     * Bean para obtener la zona horaria de Argentina como ZoneId
     * Útil para operaciones con LocalDateTime, ZonedDateTime, etc.
     */
    @Bean
    public ZoneId argentinaZoneId() {
        return ZoneId.of("America/Argentina/Buenos_Aires");
    }
} 