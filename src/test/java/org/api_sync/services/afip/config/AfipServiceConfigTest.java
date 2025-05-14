package org.api_sync.services.afip.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AfipServiceConfigTest {

    @Test
    void getDefaultConfig_ShouldReturnValidConfig() {
        // Act
        AfipServiceConfig config = AfipServiceConfig.getDefaultConfig();

        // Assert
        assertNotNull(config);
        assertEquals("https://servicios1.afip.gov.ar/wsfev1/service.asmx?WSDL", config.getSoapEndpointUrl());
        assertEquals("http://ar.gov.afip.dif.FEV1/FECAESolicitar", config.getSoapActionFecaeSolicitar());
        assertEquals(5000, config.getConnectionTimeout());
        assertEquals(7000, config.getReadTimeout());
    }

    @Test
    void builder_ShouldCreateCustomConfig() {
        // Arrange
        String customUrl = "https://custom.endpoint.com";
        String customAction = "custom/action";
        int customTimeout = 10000;

        // Act
        AfipServiceConfig config = AfipServiceConfig.builder()
            .soapEndpointUrl(customUrl)
            .soapActionFecaeSolicitar(customAction)
            .connectionTimeout(customTimeout)
            .readTimeout(customTimeout)
            .build();

        // Assert
        assertNotNull(config);
        assertEquals(customUrl, config.getSoapEndpointUrl());
        assertEquals(customAction, config.getSoapActionFecaeSolicitar());
        assertEquals(customTimeout, config.getConnectionTimeout());
        assertEquals(customTimeout, config.getReadTimeout());
    }
} 