package org.api_sync.services.afip;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class CaeErrorReportCron {

    private final CaeErrorMemory caeErrorMemory;
    private final MailService mailService;

    public CaeErrorReportCron(CaeErrorMemory caeErrorMemory, MailService mailService) {
        this.caeErrorMemory = caeErrorMemory;
        this.mailService = mailService;
    }

    // Ejecuta todos los días a las 11:30 y 19:30
    @Scheduled(cron = "0 30 11,19 * * *")
    public void sendDailyErrorReport() {
        var errores = caeErrorMemory.getAllErrors();
        if (errores.isEmpty()) return;

        String body = errores.stream()
            .map(e -> "Punto de venta: " + e.getPuntoVenta() + ", Tipo: " + e.getTipoTraducido() + ", Error: " + e.getErrorMessage())
            .collect(Collectors.joining("<br/>"));

        String destinatario = getDestinatarioFromDbMock();

        try {
            mailService.sendMail(destinatario, "Errores CAE del día", body);
            // Limpiar los errores después de enviar el mail
            errores.forEach(e -> caeErrorMemory.clearError(e.getPuntoVenta(), e.getTipo()));
        } catch (Exception ex) {
            // Manejo de error de envío de mail
            ex.printStackTrace();
        }
    }

    // Mock para obtener el destinatario desde la base de datos
    private String getDestinatarioFromDbMock() {
        return "destinatario@dominio.com";
    }
} 