package org.api_sync.adapter.inbound.gestion;

import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.services.afip.AfipConsultarCaeService;
import org.api_sync.services.afip.AfipGenerarCaeService;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CaeControllerTest {

    @Mock
    private AfipConsultarCaeService afipConsultarCaeService;
    
    @Mock
    private AfipGenerarCaeService afipGenerarCaeService;

    @InjectMocks
    private CaeController caeController;
    
    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Crear un Principal mock para las pruebas
        mockPrincipal = new TestingAuthenticationToken("testUser", "credentials");
    }

    @Test
    void ultimo_ShouldReturnLastComprobante() {
        // Arrange
        String empresa = "uuid";
        Integer puntoVenta = 1;
        Integer certificadoPuntoVenta = 1;
        Integer tipoComprobante = 0;
        Integer expectedComprobante = 12345;

        when(afipConsultarCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta, tipoComprobante))
                .thenReturn(expectedComprobante);

        // Act
        Integer result = caeController.ultimo(empresa, puntoVenta, certificadoPuntoVenta, tipoComprobante, mockPrincipal);

        // Assert
        assertEquals(expectedComprobante, result);
        verify(afipConsultarCaeService, times(1)).consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta,
                puntoVenta, tipoComprobante);
    }

    @Test
    void ultimo_WithDifferentValues_ShouldReturnLastComprobante() {
        // Arrange
        String empresa = "uuid";
        Integer puntoVenta = 2;
        Integer certificadoPuntoVenta = 2;
        Integer tipoComprobante = 0;
        Integer expectedComprobante = 54321;

        when(afipConsultarCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta, tipoComprobante))
                .thenReturn(expectedComprobante);

        // Act
        Integer result = caeController.ultimo(empresa, puntoVenta, certificadoPuntoVenta, tipoComprobante, mockPrincipal);

        // Assert
        assertEquals(expectedComprobante, result);
        verify(afipConsultarCaeService, times(1)).consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta,
                puntoVenta, tipoComprobante);
    }
    
    @Test
    void getCae_ShouldReturnCaeResponse() {
        // Arrange
        String empresa = "uuid";
        Integer certificadoPuntoVenta = 1;
        ComprobanteRequest comprobanteRequest = new ComprobanteRequest();
        CaeResponse expectedResponse = CaeResponse.builder()
                .cae("12345678901234")
                .build();
        
        when(afipGenerarCaeService.generarCae(empresa, certificadoPuntoVenta, comprobanteRequest))
                .thenReturn(expectedResponse);
                
        // Act
        CaeResponse result = caeController.getCae(empresa, certificadoPuntoVenta, comprobanteRequest, mockPrincipal);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(afipGenerarCaeService, times(1)).generarCae(empresa, certificadoPuntoVenta, comprobanteRequest);
    }
    
    @Test
    void getCae_WithDifferentValues_ShouldReturnCaeResponse() {
        // Arrange
        String empresa = "uuid";
        Integer certificadoPuntoVenta = 2;
        ComprobanteRequest comprobanteRequest = new ComprobanteRequest();
        CaeResponse expectedResponse = CaeResponse.builder()
                .cae("98765432109876")
                .caeFechaVto("20251231")
                .build();
        
        when(afipGenerarCaeService.generarCae(empresa, certificadoPuntoVenta, comprobanteRequest))
                .thenReturn(expectedResponse);
                
        // Act
        CaeResponse result = caeController.getCae(empresa, certificadoPuntoVenta, comprobanteRequest, mockPrincipal);
        
        // Assert
        assertEquals(expectedResponse, result);
        assertEquals("98765432109876", result.getCae());
        assertEquals("20251231", result.getCaeFechaVto());
        verify(afipGenerarCaeService, times(1)).generarCae(empresa, certificadoPuntoVenta, comprobanteRequest);
    }
} 