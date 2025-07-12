package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.gestion.EmpresaEmailAlerta;
import org.api_sync.services.alertas.EmpresaEmailAlertaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaeErrorReportCron {

    private final CaeErrorMemory caeErrorMemory;
    private final EmpresaEmailAlertaService empresaEmailAlertaService;
    private final MailService mailService;
    
    @Value("${cfgestion.mail.main-mail:facuenrique@gmail.com}")
    private String emailMain;

    // Ejecuta todos los días a las 11:30 y 19:30 hora de Argentina
    // La zona horaria se maneja automáticamente con America/Argentina/Buenos_Aires
    @Scheduled(cron = "0 30 11,19 * * *", zone = "America/Argentina/Buenos_Aires")
    public void sendDailyErrorReport() {
        var errores = caeErrorMemory.getAllErrors();
        if (errores.isEmpty()) {
            log.info("No hay errores de CAE para reportar");
            return;
        }

        try {
            // 1. Enviar emails específicos a cada empresa
            sendCompanySpecificReports();
            
            // 2. Enviar email general con todos los errores al email de configuración
            sendGeneralReport();
            
            // 3. Limpiar todos los errores después de enviar los reportes
            clearAllErrors();
            
        } catch (Exception ex) {
            log.error("Error enviando reportes de errores CAE: {}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Enviar reportes específicos a cada empresa
     */
    private void sendCompanySpecificReports() {
        Map<Long, List<CaeErrorMemory.ErrorInfo>> erroresPorEmpresa = caeErrorMemory.getErrorsByEmpresa();
        
        for (Map.Entry<Long, List<CaeErrorMemory.ErrorInfo>> entry : erroresPorEmpresa.entrySet()) {
            Long empresaId = entry.getKey();
            List<CaeErrorMemory.ErrorInfo> erroresEmpresa = entry.getValue();
            
            if (erroresEmpresa.isEmpty()) continue;
            
            // Crear contenido específico para la empresa
            String contenidoEmpresa = crearContenidoEmpresa(erroresEmpresa);
            String asuntoEmpresa = "Errores CAE - " + erroresEmpresa.get(0).getEmpresaNombre();
            
            try {
                // Enviar alerta específica a la empresa
                empresaEmailAlertaService.enviarAlertaEmpresa(
                    empresaId,
                    EmpresaEmailAlerta.TipoAlerta.ERROR_CAE,
                    asuntoEmpresa,
                    contenidoEmpresa
                );
                
                log.info("Reporte de errores CAE enviado a empresa {}: {} errores", 
                    empresaId, erroresEmpresa.size());
                    
            } catch (Exception e) {
                log.error("Error enviando reporte a empresa {}: {}", empresaId, e.getMessage());
            }
        }
    }
    
    /**
     * Enviar reporte general con todos los errores al email de configuración
     */
    private void sendGeneralReport() {
        var todosLosErrores = caeErrorMemory.getAllErrors();
        
        if (todosLosErrores.isEmpty()) return;
        
        // Crear contenido general con todos los errores
        String contenidoGeneral = crearContenidoGeneral(todosLosErrores.stream().toList());
        String asuntoGeneral = "Reporte General de Errores CAE - " + todosLosErrores.size() + " errores";
        
        try {
            // Enviar directamente al email de configuración
            mailService.sendMail(emailMain, asuntoGeneral, contenidoGeneral);
            
            log.info("Reporte general de errores CAE enviado a {}: {} errores totales", emailMain, todosLosErrores.size());
            
        } catch (Exception e) {
            log.error("Error enviando reporte general a {}: {}", emailMain, e.getMessage());
        }
    }
    
    /**
     * Crear contenido específico para una empresa
     */
    private String crearContenidoEmpresa(List<CaeErrorMemory.ErrorInfo> errores) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("<h2>Errores CAE - ").append(errores.get(0).getEmpresaNombre()).append("</h2>");
        contenido.append("<p>Se han detectado los siguientes errores en su empresa:</p>");
        contenido.append("<ul>");
        
        for (CaeErrorMemory.ErrorInfo error : errores) {
            contenido.append("<li><strong>Punto de Venta:</strong> ").append(error.getPuntoVenta())
                    .append(" | <strong>Tipo:</strong> ").append(error.getTipoTraducido())
                    .append(" | <strong>Error:</strong> ").append(error.getErrorMessage())
                    .append("</li>");
        }
        
        contenido.append("</ul>");
        contenido.append("<p><em>Este es un reporte automático. Por favor, revise los errores y tome las medidas necesarias.</em></p>");
        
        return contenido.toString();
    }
    
    /**
     * Crear contenido general con todos los errores
     */
    private String crearContenidoGeneral(List<CaeErrorMemory.ErrorInfo> todosLosErrores) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("<h2>Reporte General de Errores CAE</h2>");
        contenido.append("<p>Se han detectado ").append(todosLosErrores.size()).append(" errores en total:</p>");
        
        // Agrupar por empresa para mejor organización
        Map<Long, List<CaeErrorMemory.ErrorInfo>> erroresPorEmpresa = caeErrorMemory.getErrorsByEmpresa();
        
        for (Map.Entry<Long, List<CaeErrorMemory.ErrorInfo>> entry : erroresPorEmpresa.entrySet()) {
            List<CaeErrorMemory.ErrorInfo> erroresEmpresa = entry.getValue();
            String nombreEmpresa = erroresEmpresa.get(0).getEmpresaNombre();
            
            contenido.append("<h3>").append(nombreEmpresa).append(" (").append(erroresEmpresa.size()).append(" errores)</h3>");
            contenido.append("<ul>");
            
            for (CaeErrorMemory.ErrorInfo error : erroresEmpresa) {
                contenido.append("<li><strong>Punto de Venta:</strong> ").append(error.getPuntoVenta())
                        .append(" | <strong>Tipo:</strong> ").append(error.getTipoTraducido())
                        .append(" | <strong>Error:</strong> ").append(error.getErrorMessage())
                        .append("</li>");
            }
            
            contenido.append("</ul>");
        }
        
        contenido.append("<p><em>Este es un reporte automático del sistema. Los errores han sido enviados a cada empresa correspondiente.</em></p>");
        
        return contenido.toString();
    }
    
    /**
     * Limpiar todos los errores después de enviar los reportes
     */
    private void clearAllErrors() {
        var todosLosErrores = caeErrorMemory.getAllErrors();
        
        for (CaeErrorMemory.ErrorInfo error : todosLosErrores) {
            caeErrorMemory.clearError(error.getPuntoVenta(), error.getTipo());
        }
        
        log.info("Todos los errores de CAE han sido limpiados después del reporte");
    }
} 