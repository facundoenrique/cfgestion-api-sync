package org.api_sync.adapter.inbound.gestion;

import org.api_sync.services.afip.AfipCaeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CaeControllerTest {

    @Mock
    private AfipCaeService afipCaeService;

    @InjectMocks
    private CaeController caeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ultimo_ShouldReturnLastComprobante() {
        // Arrange
        Long empresa = 1L;
        Integer puntoVenta = 1;
        Integer certificadoPuntoVenta = 1;
        Integer expectedComprobante = 12345;

        when(afipCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta))
                .thenReturn(expectedComprobante);

        // Act
        Integer result = caeController.ultimo(empresa, puntoVenta, certificadoPuntoVenta);

        // Assert
        assertEquals(expectedComprobante, result);
        verify(afipCaeService, times(1)).consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta);
    }

    @Test
    void ultimo_WithDifferentValues_ShouldReturnLastComprobante() {
        // Arrange
        Long empresa = 2L;
        Integer puntoVenta = 2;
        Integer certificadoPuntoVenta = 2;
        Integer expectedComprobante = 54321;

        when(afipCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta))
                .thenReturn(expectedComprobante);

        // Act
        Integer result = caeController.ultimo(empresa, puntoVenta, certificadoPuntoVenta);

        // Assert
        assertEquals(expectedComprobante, result);
        verify(afipCaeService, times(1)).consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta);
    }
} 