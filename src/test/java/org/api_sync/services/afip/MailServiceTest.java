package org.api_sync.services.afip;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MailServiceTest {
    @Test
    void testSendMail() throws Exception {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MailService mailService = new MailService(mailSender);
        String to = "facuenrique@gmail.com";
        String subject = "Asunto de prueba";
        String body = "Cuerpo del mail";

        mailService.sendMail(to, subject, body);

        verify(mailSender, times(1)).send(mimeMessage);
    }
} 