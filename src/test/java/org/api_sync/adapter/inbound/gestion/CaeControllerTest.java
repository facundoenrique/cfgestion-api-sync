package org.api_sync.adapter.inbound.gestion;

import org.api_sync.services.afip.AfipConsultarCaeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CaeControllerTest {

    @Mock
    private AfipConsultarCaeService afipCaeService;

    @InjectMocks
    private CaeController caeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ultimo_ShouldReturnLastComprobante() {
        // Arrange
        String empresa = "uuid";
        Integer puntoVenta = 1;
        Integer certificadoPuntoVenta = 1;
        Integer expectedComprobante = 12345;

        when(afipCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta, 0))
                .thenReturn(expectedComprobante);

        // Act
        Integer result = caeController.ultimo(empresa, puntoVenta, certificadoPuntoVenta, 0);

        // Assert
        assertEquals(expectedComprobante, result);
        verify(afipCaeService, times(1)).consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta,
                puntoVenta, 0);
    }

    @Test
    void ultimo_WithDifferentValues_ShouldReturnLastComprobante() {
        // Arrange
        String empresa = "uuid";
        Integer puntoVenta = 2;
        Integer certificadoPuntoVenta = 2;
        Integer expectedComprobante = 54321;

        when(afipCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta, 0))
                .thenReturn(expectedComprobante);

        // Act
        Integer result = caeController.ultimo(empresa, puntoVenta, certificadoPuntoVenta, 0);

        // Assert
        assertEquals(expectedComprobante, result);
        verify(afipCaeService, times(1)).consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta,
                puntoVenta, 0);
    }
} 