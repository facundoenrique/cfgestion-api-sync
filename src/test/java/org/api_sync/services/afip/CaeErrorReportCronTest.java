package org.api_sync.services.afip;

import org.api_sync.adapter.outbound.entities.gestion.EmpresaEmailAlerta;
import org.api_sync.services.alertas.EmpresaEmailAlertaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaeErrorReportCronTest {

    @Mock
    private CaeErrorMemory caeErrorMemory;

    @Mock
    private EmpresaEmailAlertaService empresaEmailAlertaService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private CaeErrorReportCron caeErrorReportCron;

    private CaeErrorMemory.ErrorInfo error1;
    private CaeErrorMemory.ErrorInfo error2;

    @BeforeEach
    void setUp() {
        // Crear errores de ejemplo
        error1 = new CaeErrorMemory.ErrorInfo(1, 1, "Error de validación", 1L, "Empresa A");
        error2 = new CaeErrorMemory.ErrorInfo(2, 5, "Error de conexión", 2L, "Empresa B");
    }

    @Test
    void sendDailyErrorReport_ConErrores_DeberiaEnviarReportesEspecificosYGenerales() throws Exception {
        // Given
        List<CaeErrorMemory.ErrorInfo> errores = Arrays.asList(error1, error2);
        when(caeErrorMemory.getAllErrors()).thenReturn(errores);
        when(caeErrorMemory.getErrorsByEmpresa()).thenReturn(
            java.util.Map.of(
                1L, Arrays.asList(error1),
                2L, Arrays.asList(error2)
            )
        );

        // When
        caeErrorReportCron.sendDailyErrorReport();

        // Then
        // Verificar que se enviaron reportes específicos a cada empresa
        verify(empresaEmailAlertaService).enviarAlertaEmpresa(
            eq(1L), 
            eq(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE), 
            anyString(), 
            anyString()
        );
        
        verify(empresaEmailAlertaService).enviarAlertaEmpresa(
            eq(2L), 
            eq(EmpresaEmailAlerta.TipoAlerta.ERROR_CAE), 
            anyString(), 
            anyString()
        );
        
        // Verificar que se envió reporte general al email de configuración
        verify(mailService).sendMail(
            eq("facuenrique@gmail.com"), 
            anyString(), 
            anyString()
        );
        
        // Verificar que se limpiaron los errores
        verify(caeErrorMemory, times(2)).clearError(anyInt(), anyInt());
    }

    @Test
    void sendDailyErrorReport_SinErrores_NoDeberiaEnviarReportes() throws Exception {
        // Given
        when(caeErrorMemory.getAllErrors()).thenReturn(Arrays.asList());

        // When
        caeErrorReportCron.sendDailyErrorReport();

        // Then
        verify(empresaEmailAlertaService, never()).enviarAlertaEmpresa(any(), any(), any(), any());
        verify(mailService, never()).sendMail(any(), any(), any());
        verify(caeErrorMemory, never()).clearError(anyInt(), anyInt());
    }

    @Test
    void sendDailyErrorReport_ConErrorEnEnvio_DeberiaManejarExcepcion() {
        // Given
        List<CaeErrorMemory.ErrorInfo> errores = Arrays.asList(error1);
        when(caeErrorMemory.getAllErrors()).thenReturn(errores);
        when(caeErrorMemory.getErrorsByEmpresa()).thenReturn(
            java.util.Map.of(1L, Arrays.asList(error1))
        );
        doThrow(new RuntimeException("Error de email")).when(empresaEmailAlertaService)
            .enviarAlertaEmpresa(any(), any(), any(), any());

        // When & Then
        // No debería lanzar excepción, solo loggear el error
        caeErrorReportCron.sendDailyErrorReport();
        
        // Verificar que se intentó enviar el reporte
        verify(empresaEmailAlertaService).enviarAlertaEmpresa(any(), any(), any(), any());
    }
} 