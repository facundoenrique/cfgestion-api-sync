package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaeErrorReportCron {

    private final CaeErrorMemory caeErrorMemory;
    private final MailService mailService;
    @Value("${cfgestion.mail.main-mail}")
    private String emailMain;

    // Ejecuta todos los días a las 11:30 y 19:30
    @Scheduled(cron = "0 30 11,19 * * *")
    public void sendDailyErrorReport() {
        var errores = caeErrorMemory.getAllErrors();
        if (errores.isEmpty()) return;

        String body = errores.stream()
            .map(e -> "Punto de venta: " + e.getPuntoVenta() + ", Tipo: " + e.getTipoTraducido() + ", Error: " + e.getErrorMessage())
            .collect(Collectors.joining("<br/>"));
        
        try {
            String destinatario = StringUtils.isNotBlank(emailMain) ? emailMain : "facuenrique@gmail.com";
            log.info("Enviando email con body :{}", body);
            mailService.sendMail(destinatario, "Errores CAE del día", body);
            // Limpiar los errores después de enviar el mail
            errores.forEach(e -> caeErrorMemory.clearError(e.getPuntoVenta(), e.getTipo()));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

} 