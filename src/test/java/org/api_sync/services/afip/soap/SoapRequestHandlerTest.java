package org.api_sync.services.afip.soap;

import org.api_sync.services.afip.config.AfipServiceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoapRequestHandlerTest {

    @Mock
    private SOAPConnection soapConnection;

    @Mock
    private SOAPConnectionFactory soapConnectionFactory;

    private SoapRequestHandler requestHandler;
    private AfipServiceConfig config;
    private MockedStatic<SOAPConnectionFactory> mockedStatic;
    private SOAPMessage soapMessage;

    @BeforeEach
    void setUp() throws SOAPException {
        config = AfipServiceConfig.getDefaultConfig();
        requestHandler = new SoapRequestHandler(config);
        
        // Mock SOAPConnectionFactory
        mockedStatic = mockStatic(SOAPConnectionFactory.class);
        mockedStatic.when(SOAPConnectionFactory::newInstance).thenReturn(soapConnectionFactory);
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        
        // Create test SOAP message
        String soapContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soapenv:Body>" +
                "<test>content</test>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        
        MessageFactory messageFactory = MessageFactory.newInstance();
        soapMessage = messageFactory.createMessage();
        soapMessage.getSOAPPart().setContent(new javax.xml.transform.stream.StreamSource(
            new java.io.StringReader(soapContent)
        ));
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    void executeSoapRequest_Success() throws Exception {
        // Arrange
        String endpointUrl = "https://test.endpoint.com";
        String soapAction = "test/action";
        
        // Create a real SOAPMessage for the response
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage responseMessage = factory.createMessage();
        SOAPBody body = responseMessage.getSOAPBody();
        
        // Add response content in a way that's compatible with Axis
        SOAPElement responseElement = body.addChildElement("Response", "ns", "http://test.namespace.com");
        responseElement.addTextNode("Test response");
        
        when(soapConnection.call(any(SOAPMessage.class), any(URL.class))).thenReturn(responseMessage);

        // Act
        SOAPMessage result = requestHandler.executeSoapRequest(endpointUrl, soapAction, soapMessage);

        // Assert
        assertNotNull(result);
        
        // Verify response content
        SOAPBody resultBody = result.getSOAPBody();
        assertNotNull(resultBody);
        assertTrue(resultBody.hasChildNodes());
        
        // Verify other aspects
        verify(soapConnection).call(any(SOAPMessage.class), any(URL.class));
        verify(soapConnection).close();
    }

    @Test
    void executeSoapRequest_ThrowsSOAPException() throws Exception {
        // Arrange
        String endpointUrl = "https://test.endpoint.com";
        String soapAction = "test/action";
        
        when(soapConnection.call(any(SOAPMessage.class), any(URL.class)))
            .thenThrow(new SOAPException("Test error"));

        // Act & Assert
        SOAPException exception = assertThrows(SOAPException.class, () -> 
            requestHandler.executeSoapRequest(endpointUrl, soapAction, soapMessage));
        assertEquals("Test error", exception.getMessage());
        verify(soapConnection).close();
    }
} 