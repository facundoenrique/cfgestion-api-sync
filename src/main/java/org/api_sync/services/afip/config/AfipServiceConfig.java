package org.api_sync.services.afip.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AfipServiceConfig {
    private final String soapEndpointUrl;
    private final String soapActionFecaeSolicitar;
    private final int connectionTimeout;
    private final int readTimeout;

    public static AfipServiceConfig getDefaultConfig() {
        return AfipServiceConfig.builder()
            .soapEndpointUrl("https://servicios1.afip.gov.ar/wsfev1/service.asmx?WSDL")
            .soapActionFecaeSolicitar("http://ar.gov.afip.dif.FEV1/FECAESolicitar")
            .connectionTimeout(5000)
            .readTimeout(5000)
            .build();
    }
} 