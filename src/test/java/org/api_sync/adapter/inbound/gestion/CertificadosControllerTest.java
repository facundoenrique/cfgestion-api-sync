package org.api_sync.adapter.inbound.gestion;

import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.services.certificados.CertificadosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CertificadosControllerTest {

    @Mock
    private CertificadosService certificadoService;

    @InjectMocks
    private CertificadosController certificadosController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void subirCertificado_ShouldReturnSuccessMessage() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.crt",
                "application/x-x509-ca-cert",
                "test certificate content".getBytes()
        );
        Integer puntoVenta = 1;
        Long empresaId = 1L;
        String password = "test123";

        Certificado certificado = new Certificado();
        certificado.setId(1L);
        certificado.setPuntoVenta(puntoVenta);

        when(certificadoService.guardarCertificado(any(), eq(puntoVenta), eq(empresaId), eq(password)))
                .thenReturn(certificado);

        // Act
        ResponseEntity<String> response = certificadosController.subirCertificado(file, puntoVenta, empresaId, password);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Certificado guardado con ID: 1", response.getBody());
        verify(certificadoService, times(1)).guardarCertificado(file, puntoVenta, empresaId, password);
    }

    @Test
    void subirCertificado_WhenErrorOccurs_ShouldReturnErrorMessage() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.crt",
                "application/x-x509-ca-cert",
                "test certificate content".getBytes()
        );
        Integer puntoVenta = 1;
        Long empresaId = 1L;
        String password = "test123";

        when(certificadoService.guardarCertificado(any(), eq(puntoVenta), eq(empresaId), eq(password)))
                .thenThrow(new RuntimeException("Error al procesar el certificado"));

        // Act
        ResponseEntity<String> response = certificadosController.subirCertificado(file, puntoVenta, empresaId, password);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Error al guardar el certificado: Error al procesar el certificado", response.getBody());
        verify(certificadoService, times(1)).guardarCertificado(file, puntoVenta, empresaId, password);
    }
} 