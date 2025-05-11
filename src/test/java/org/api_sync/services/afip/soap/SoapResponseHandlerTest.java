package org.api_sync.services.afip.soap;

import org.api_sync.services.afip.model.CaeDTO;
import org.api_sync.services.afip.exceptions.AfipServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoapResponseHandlerTest {

    private SoapResponseHandler responseHandler;
    private static final String TEST_XML = "<test>response</test>";

    @BeforeEach
    void setUp() {
        responseHandler = new SoapResponseHandler();
    }

    @Test
    void handleCaeResponse_Success() throws Exception {
        // Arrange
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECAESolicitarResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<FECAESolicitarResult>" +
                "<Resultado>A</Resultado>" +
                "<CAE>12345678901234</CAE>" +
                "<CAEFchVto>20240315</CAEFchVto>" +
                "</FECAESolicitarResult>" +
                "</FECAESolicitarResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);

        // Act
        CaeDTO result = responseHandler.handleCaeResponse(soapMessage);

        // Assert
        assertNotNull(result);
        assertEquals("12345678901234", result.getCae());
        assertEquals("20240315", result.getCaeFchVto());
    }

    @Test
    void handleCaeResponse_Error() throws Exception {
        // Arrange
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECAESolicitarResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<FECAESolicitarResult>" +
                "<Resultado>R</Resultado>" +
                "<Errors>" +
                "<Err>" +
                "<Code>1001</Code>" +
                "<Msg>Error de validacion</Msg>" +
                "</Err>" +
                "</Errors>" +
                "</FECAESolicitarResult>" +
                "</FECAESolicitarResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);

        // Act & Assert
        assertThrows(AfipServiceException.class, () -> responseHandler.handleCaeResponse(soapMessage));
    }

    private SOAPMessage createSoapMessage(String xmlContent) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        soapMessage.getSOAPPart().setContent(new javax.xml.transform.stream.StreamSource(
            new java.io.StringReader(xmlContent)
        ));
        return soapMessage;
    }
} 