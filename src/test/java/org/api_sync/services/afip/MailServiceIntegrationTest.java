package org.api_sync.services.afip;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Solo ejecutar manualmente para pruebas reales de envío de mail")
public class MailServiceIntegrationTest {

    @Autowired
    private MailService mailService;

    @Test
    void sendRealMail() throws Exception {
        String to = "facuenrique@gmail.com"; // Cambia por tu mail si lo deseas
        String subject = "Prueba real de integración MailService";
        String body = "Punto de venta: 1, Tipo: 11, Error: [1001] CAE rechazado por AFIP; <br/>" +
                      "Punto de venta: 2, Tipo: 6, Error: [2002] CUIT inválido; <br/>" +
                      "Punto de venta: 3, Tipo: 1, Error: [3003] Fecha fuera de rango; <br/>";
        mailService.sendMail(to, subject, body);
    }
} 