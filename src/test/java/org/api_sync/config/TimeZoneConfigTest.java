package org.api_sync.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TimeZoneConfigTest {

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    @Test
    void testArgentinaTimeZoneConfiguration() {
        // Verificar que la zona horaria por defecto es Argentina
        TimeZone defaultTimeZone = TimeZone.getDefault();
        assertEquals("America/Argentina/Buenos_Aires", defaultTimeZone.getID());
        
        // Verificar que el bean de zona horaria es correcto
        TimeZone argentinaTz = timeZoneConfig.argentinaTimeZone();
        assertEquals("America/Argentina/Buenos_Aires", argentinaTz.getID());
        
        // Verificar que el ZoneId es correcto
        ZoneId argentinaZoneId = timeZoneConfig.argentinaZoneId();
        assertEquals("America/Argentina/Buenos_Aires", argentinaZoneId.getId());
    }

    @Test
    void testCronJobTimeCalculation() {
        // Verificar que las 11:30 y 19:30 hora de Argentina se calculan correctamente
        ZoneId argentinaZone = ZoneId.of("America/Argentina/Buenos_Aires");
        
        // Crear fechas en hora de Argentina
        LocalDateTime argentinaTime1130 = LocalDateTime.of(2024, 1, 15, 11, 30);
        LocalDateTime argentinaTime1930 = LocalDateTime.of(2024, 1, 15, 19, 30);
        
        // Convertir a UTC para verificar
        ZonedDateTime argentinaZoned1130 = ZonedDateTime.of(argentinaTime1130, argentinaZone);
        ZonedDateTime argentinaZoned1930 = ZonedDateTime.of(argentinaTime1930, argentinaZone);
        
        ZonedDateTime utc1130 = argentinaZoned1130.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utc1930 = argentinaZoned1930.withZoneSameInstant(ZoneId.of("UTC"));
        
        // En enero (horario de verano), Argentina está en UTC-2
        // En julio (horario estándar), Argentina está en UTC-3
        // El test verifica que la conversión funciona correctamente
        assertNotNull(utc1130);
        assertNotNull(utc1930);
        
        // Verificar que hay diferencia de zona horaria
        assertNotEquals(argentinaTime1130.getHour(), utc1130.getHour());
        assertNotEquals(argentinaTime1930.getHour(), utc1930.getHour());
    }

    @Test
    void testDaylightSavingTimeHandling() {
        // Verificar que la zona horaria maneja correctamente el horario de verano
        ZoneId argentinaZone = ZoneId.of("America/Argentina/Buenos_Aires");
        
        // Enero (horario de verano) - UTC-2
        ZonedDateTime summerTime = ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, argentinaZone);
        ZonedDateTime summerTimeUTC = summerTime.withZoneSameInstant(ZoneId.of("UTC"));
        assertEquals(14, summerTimeUTC.getHour()); // 12:00 ARG = 14:00 UTC
        
        // Julio (horario estándar) - UTC-3
        ZonedDateTime winterTime = ZonedDateTime.of(2024, 7, 15, 12, 0, 0, 0, argentinaZone);
        ZonedDateTime winterTimeUTC = winterTime.withZoneSameInstant(ZoneId.of("UTC"));
        assertEquals(15, winterTimeUTC.getHour()); // 12:00 ARG = 15:00 UTC
    }
} 