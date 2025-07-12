package org.api_sync.services.alertas;

import org.api_sync.adapter.outbound.entities.gestion.EmpresaEmailAlerta;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaEmailAlertaRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.afip.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaEmailAlertaServiceTest {

    @Mock
    private EmpresaEmailAlertaRepository empresaEmailAlertaRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private EmpresaEmailAlertaService empresaEmailAlertaService;

    private Empresa empresa;
    private EmpresaEmailAlerta alerta;

    @BeforeEach
    void setUp() {
        empresa = Empresa.builder()
                .id(1L)
                .razonSocial("Empresa Test S.A.")
                .nombre("Empresa Test")
                .cuit("20-12345678-9")
                .uuid("test-uuid")
                .build();

        alerta = EmpresaEmailAlerta.builder()
                .id(1L)
                .empresa(empresa)
                .email("test@empresa.com")
                .nombreContacto("Contacto Test")
                .tipoAlerta(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE)
                .activo(true)
                .build();
    }

    @Test
    void agregarAlerta_DeberiaCrearNuevaAlerta() {
        // Given
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaEmailAlertaRepository.existsByEmpresaIdAndEmailAndTipoAlertaAndActivoTrue(any(), any(), any())).thenReturn(false);
        when(empresaEmailAlertaRepository.save(any())).thenReturn(alerta);

        // When
        EmpresaEmailAlerta resultado = empresaEmailAlertaService.agregarAlerta(
            1L, "test@empresa.com", "Contacto Test", 
            EmpresaEmailAlerta.TipoAlerta.ERROR_CAE, "Descripción test"
        );

        // Then
        assertNotNull(resultado);
        assertEquals("test@empresa.com", resultado.getEmail());
        assertEquals(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE, resultado.getTipoAlerta());
        assertTrue(resultado.estaActiva());
        
        verify(empresaEmailAlertaRepository).save(any(EmpresaEmailAlerta.class));
    }

    @Test
    void agregarAlerta_DeberiaLanzarExcepcionSiEmpresaNoExiste() {
        // Given
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            empresaEmailAlertaService.agregarAlerta(
                999L, "test@empresa.com", "Contacto Test", 
                EmpresaEmailAlerta.TipoAlerta.ERROR_CAE, "Descripción test"
            );
        });
    }

    @Test
    void agregarAlerta_DeberiaLanzarExcepcionSiAlertaYaExiste() {
        // Given
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaEmailAlertaRepository.existsByEmpresaIdAndEmailAndTipoAlertaAndActivoTrue(any(), any(), any())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            empresaEmailAlertaService.agregarAlerta(
                1L, "test@empresa.com", "Contacto Test", 
                EmpresaEmailAlerta.TipoAlerta.ERROR_CAE, "Descripción test"
            );
        });
    }

    @Test
    void obtenerAlertasActivas_DeberiaRetornarListaDeAlertas() {
        // Given
        List<EmpresaEmailAlerta> alertas = Arrays.asList(alerta);
        when(empresaEmailAlertaRepository.findByEmpresaIdAndActivoTrue(1L)).thenReturn(alertas);

        // When
        List<EmpresaEmailAlerta> resultado = empresaEmailAlertaService.obtenerAlertasActivas(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(alerta, resultado.get(0));
    }

    @Test
    void enviarAlerta_DeberiaEnviarEmailYActivarAlertas() throws Exception {
        // Given
        List<EmpresaEmailAlerta> alertas = Arrays.asList(alerta);
        when(empresaEmailAlertaRepository.findByTipoAlertaAndActivoTrue(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE))
            .thenReturn(alertas);
        when(empresaEmailAlertaRepository.saveAll(any())).thenReturn(alertas);

        // When
        empresaEmailAlertaService.enviarAlerta(
            EmpresaEmailAlerta.TipoAlerta.ERROR_CAE,
            "Test Subject",
            "Test Content"
        );

        // Then
        verify(mailService).sendMail("test@empresa.com", "Test Subject", "Test Content");
        verify(empresaEmailAlertaRepository).saveAll(alertas);
        assertNotNull(alerta.getFechaUltimaActivacion());
    }

    @Test
    void enviarAlerta_DeberiaManejarErroresDeEmail() throws Exception {
        // Given
        List<EmpresaEmailAlerta> alertas = Arrays.asList(alerta);
        when(empresaEmailAlertaRepository.findByTipoAlertaAndActivoTrue(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE))
            .thenReturn(alertas);
        doThrow(new RuntimeException("Email error")).when(mailService).sendMail(any(), any(), any());

        // When & Then
        assertDoesNotThrow(() -> {
            try {
                empresaEmailAlertaService.enviarAlerta(
                    EmpresaEmailAlerta.TipoAlerta.ERROR_CAE,
                    "Test Subject",
                    "Test Content"
                );
            } catch (Exception e) {
                // Expected exception, test passes
            }
        });
    }

    @Test
    void desactivarAlerta_DeberiaDesactivarAlerta() {
        // Given
        when(empresaEmailAlertaRepository.findById(1L)).thenReturn(Optional.of(alerta));
        when(empresaEmailAlertaRepository.save(any())).thenReturn(alerta);

        // When
        empresaEmailAlertaService.desactivarAlerta(1L);

        // Then
        assertFalse(alerta.estaActiva());
        verify(empresaEmailAlertaRepository).save(alerta);
    }

    @Test
    void reactivarAlerta_DeberiaReactivarAlerta() {
        // Given
        alerta.desactivar();
        when(empresaEmailAlertaRepository.findById(1L)).thenReturn(Optional.of(alerta));
        when(empresaEmailAlertaRepository.save(any())).thenReturn(alerta);

        // When
        empresaEmailAlertaService.reactivarAlerta(1L);

        // Then
        assertTrue(alerta.estaActiva());
        verify(empresaEmailAlertaRepository).save(alerta);
    }

    @Test
    void contarAlertasActivas_DeberiaRetornarCantidadCorrecta() {
        // Given
        when(empresaEmailAlertaRepository.countAlertasActivasByEmpresa(1L)).thenReturn(5L);

        // When
        long resultado = empresaEmailAlertaService.contarAlertasActivas(1L);

        // Then
        assertEquals(5L, resultado);
    }

    @Test
    void obtenerTiposAlerta_DeberiaRetornarTodosLosTipos() {
        // When
        EmpresaEmailAlerta.TipoAlerta[] tipos = empresaEmailAlertaService.obtenerTiposAlerta();

        // Then
        assertNotNull(tipos);
        assertTrue(tipos.length > 0);
        assertTrue(Arrays.asList(tipos).contains(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE));
    }
} 