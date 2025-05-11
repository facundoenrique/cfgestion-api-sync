package org.api_sync.services.afip;

import org.api_sync.services.afip.config.AfipServiceConfig;
import org.api_sync.services.afip.model.CaeDTO;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.api_sync.services.afip.soap.SoapMessageFactory;
import org.api_sync.services.afip.soap.SoapRequestHandler;
import org.api_sync.services.afip.soap.SoapResponseHandler;
import org.api_sync.services.afip.exceptions.AfipServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PSOAPClientSAAJTest {

    private static final String TOKEN = "test-token";
    private static final String SIGN = "test-sign";
    private static final String CUIT = "12345678901";

    @Mock
    private SoapMessageFactory messageFactory;

    @Mock
    private SoapRequestHandler requestHandler;

    @Mock
    private SoapResponseHandler responseHandler;

    @Mock
    private SOAPConnection soapConnection;

    @Mock
    private SOAPConnectionFactory soapConnectionFactory;

    private PSOAPClientSAAJ client;
    private MockedStatic<SOAPConnectionFactory> mockedStatic;
    private MessageFactory soapMessageFactory;
    private AfipServiceConfig config;

    @BeforeAll
    static void setUpLogging() {
        // Deshabilitar todos los logs de Spring y Commons Logging
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons", "OFF");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.springframework", "OFF");
        System.setProperty("org.apache.commons.logging.simplelog.defaultlog", "OFF");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "false");
        System.setProperty("org.apache.commons.logging.simplelog.showlogname", "false");
        System.setProperty("org.apache.commons.logging.simplelog.showShortLogname", "false");
        System.setProperty("org.apache.commons.logging.simplelog.showthreadname", "false");
        
        // Configurar los loggers
        Logger.getLogger("org.apache.commons.logging").setLevel(Level.OFF);
        Logger.getLogger("org.springframework.jcl").setLevel(Level.OFF);
        Logger.getLogger("org.springframework.core").setLevel(Level.OFF);
        Logger.getLogger("org.springframework.util").setLevel(Level.OFF);
        Logger.getLogger("org.springframework.beans").setLevel(Level.OFF);
        Logger.getLogger("org.springframework.context").setLevel(Level.OFF);
        Logger.getLogger("org.api_sync.services.afip.PSOAPClientSAAJ").setLevel(Level.OFF);
    }

    @BeforeEach
    void setUp() throws Exception {
        config = AfipServiceConfig.getDefaultConfig();
        
        // Crear el cliente con los mocks
        client = new PSOAPClientSAAJ(TOKEN, SIGN, CUIT, config);
        
        // Inyectar los mocks usando reflection
        java.lang.reflect.Field messageFactoryField = PSOAPClientSAAJ.class.getDeclaredField("messageFactory");
        java.lang.reflect.Field requestHandlerField = PSOAPClientSAAJ.class.getDeclaredField("requestHandler");
        java.lang.reflect.Field responseHandlerField = PSOAPClientSAAJ.class.getDeclaredField("responseHandler");
        
        messageFactoryField.setAccessible(true);
        requestHandlerField.setAccessible(true);
        responseHandlerField.setAccessible(true);
        
        messageFactoryField.set(client, messageFactory);
        requestHandlerField.set(client, requestHandler);
        responseHandlerField.set(client, responseHandler);

        // Mock SOAPConnectionFactory
        mockedStatic = mockStatic(SOAPConnectionFactory.class);
        mockedStatic.when(SOAPConnectionFactory::newInstance).thenReturn(soapConnectionFactory);

        // Inicializar MessageFactory
        soapMessageFactory = MessageFactory.newInstance();
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    void getCae_Success() throws Exception {
        // Arrange
        ComprobanteRequest request = createTestComprobanteRequest();
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
        CaeDTO expectedCae = new CaeDTO();
        expectedCae.setCae("12345678901234");
        expectedCae.setCaeFchVto("20240315");
        
        // Configurar los mocks uno por uno
        MockedStatic<SoapMessageFactory> mockedMessageFactory = mockStatic(SoapMessageFactory.class);
        mockedMessageFactory.when(() -> SoapMessageFactory.createFECAESolicitarMessage(request)).thenReturn(soapMessage);
        
        when(requestHandler.executeSoapRequest(
            config.getSoapEndpointUrl(),
            config.getSoapActionFecaeSolicitar(),
            soapMessage
        )).thenReturn(soapMessage);
        
        when(responseHandler.handleCaeResponse(soapMessage)).thenReturn(expectedCae);

        try {
            // Act
            CaeDTO result = client.getCae(request);

            // Assert
            assertNotNull(result, "El resultado no debería ser null");
            assertEquals("12345678901234", result.getCae(), "El CAE no coincide");
            assertEquals("20240315", result.getCaeFchVto(), "La fecha de vencimiento no coincide");
            
            // Verificar las llamadas a los mocks
            mockedMessageFactory.verify(() -> SoapMessageFactory.createFECAESolicitarMessage(request));
            verify(requestHandler).executeSoapRequest(
                config.getSoapEndpointUrl(),
                config.getSoapActionFecaeSolicitar(),
                soapMessage
            );
            verify(responseHandler).handleCaeResponse(soapMessage);
        } finally {
            mockedMessageFactory.close();
        }
    }

    @Test
    void getCae_ErrorResponse() throws Exception {
        // Arrange
        ComprobanteRequest request = createTestComprobanteRequest();
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
        CaeDTO expectedCae = new CaeDTO();
        expectedCae.setCae(null);
        expectedCae.setCaeFchVto(null);

        // Configurar los mocks uno por uno
        MockedStatic<SoapMessageFactory> mockedMessageFactory = mockStatic(SoapMessageFactory.class);
        mockedMessageFactory.when(() -> SoapMessageFactory.createFECAESolicitarMessage(request)).thenReturn(soapMessage);
        
        when(requestHandler.executeSoapRequest(
            config.getSoapEndpointUrl(),
            config.getSoapActionFecaeSolicitar(),
            soapMessage
        )).thenReturn(soapMessage);
        
        when(responseHandler.handleCaeResponse(soapMessage)).thenReturn(expectedCae);

        try {
            // Act
            CaeDTO result = client.getCae(request);

            // Assert
            assertNotNull(result, "El resultado no debería ser null");
            assertNull(result.getCae(), "El CAE debería ser null en caso de error");
            assertNull(result.getCaeFchVto(), "La fecha de vencimiento debería ser null en caso de error");
            
            // Verificar las llamadas a los mocks
            mockedMessageFactory.verify(() -> SoapMessageFactory.createFECAESolicitarMessage(request));
            verify(requestHandler).executeSoapRequest(
                config.getSoapEndpointUrl(),
                config.getSoapActionFecaeSolicitar(),
                soapMessage
            );
            verify(responseHandler).handleCaeResponse(soapMessage);
        } finally {
            mockedMessageFactory.close();
        }
    }

    @Test
    void getCae_ThrowsException() throws Exception {
        // Arrange
        ComprobanteRequest request = createTestComprobanteRequest();
        RuntimeException expectedException = new RuntimeException("Test error");

        // Configurar los mocks uno por uno
        MockedStatic<SoapMessageFactory> mockedMessageFactory = mockStatic(SoapMessageFactory.class);
        mockedMessageFactory.when(() -> SoapMessageFactory.createFECAESolicitarMessage(request))
            .thenThrow(expectedException);

        try {
            // Act & Assert
            assertThrows(AfipServiceException.class, () -> client.getCae(request));
            
            // Verificar las llamadas a los mocks
            mockedMessageFactory.verify(() -> SoapMessageFactory.createFECAESolicitarMessage(request));
        } finally {
            mockedMessageFactory.close();
        }
    }

    @Test
    void searchUltimaFacturaElectronica_Success() throws Exception {
        // Arrange
        int puntoVenta = 1;
        int tipoComprobante = 1;
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECompUltimoAutorizadoResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<FECompUltimoAutorizadoResult>" +
                "<CbteNro>123</CbteNro>" +
                "</FECompUltimoAutorizadoResult>" +
                "</FECompUltimoAutorizadoResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);
        
        // Configurar el mock de soapConnection
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        when(soapConnection.call(any(SOAPMessage.class), any())).thenReturn(soapMessage);

        // Act
        Integer result = client.searchUltimaFacturaElectronica(puntoVenta, tipoComprobante);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(123, result, "El número de comprobante no coincide");
        verify(soapConnection).close();
    }

    @Test
    void searchUltimaFacturaElectronica_Error() throws Exception {
        // Arrange
        int puntoVenta = 1;
        int tipoComprobante = 1;
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECompUltimoAutorizadoResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<FECompUltimoAutorizadoResult>" +
                "<Errors>" +
                "<Err>" +
                "<Code>1001</Code>" +
                "<Msg>Error de validacion</Msg>" +
                "</Err>" +
                "</Errors>" +
                "</FECompUltimoAutorizadoResult>" +
                "</FECompUltimoAutorizadoResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);
        
        // Configurar el mock de soapConnection
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        when(soapConnection.call(any(SOAPMessage.class), any())).thenReturn(soapMessage);

        // Act & Assert
        assertThrows(AfipServiceException.class, () -> client.searchUltimaFacturaElectronica(puntoVenta, tipoComprobante));
        verify(soapConnection).close();
    }

    @Test
    void llamarFECompConsultar_Success() throws Exception {
        // Arrange
        int puntoVenta = 1;
        int tipoComprobante = 1;
        int numeroComprobante = 123;
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECompConsultarResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<FECompConsultarResult>" +
                "<Resultado>A</Resultado>" +
                "<CbteFch>20240315</CbteFch>" +
                "<ImpTotal>100.00</ImpTotal>" +
                "<ImpNeto>82.64</ImpNeto>" +
                "<ImpIVA>17.36</ImpIVA>" +
                "<ImpTrib>0.00</ImpTrib>" +
                "<FchProceso>20240315</FchProceso>" +
                "<CodAutorizacion>12345678901234</CodAutorizacion>" +
                "</FECompConsultarResult>" +
                "</FECompConsultarResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);
        
        // Configurar el mock de soapConnection
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        doReturn(soapMessage).when(soapConnection).call(any(SOAPMessage.class), any(URL.class));

        // Act
        ComprobanteAfip result = client.llamarFECompConsultar(puntoVenta, tipoComprobante, numeroComprobante);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(puntoVenta, result.getPunto_venta(), "El punto de venta no coincide");
        assertEquals(numeroComprobante, result.getNroComp(), "El número de comprobante no coincide");
        assertEquals(tipoComprobante, result.getTipoComp(), "El tipo de comprobante no coincide");
        assertEquals("20240315", result.getCbteFch(), "La fecha del comprobante no coincide");
        assertEquals(100.00, result.getImpTotal(), "El importe total no coincide");
        assertEquals(82.64, result.getImpNeto(), "El importe neto no coincide");
        assertEquals(17.36, result.getImpIvas(), "El importe de IVA no coincide");
        assertEquals(0.00, result.getImpTributos(), "El importe de tributos no coincide");
        assertEquals("20240315", result.getFechaProc(), "La fecha de proceso no coincide");
        assertEquals(12345678901234L, result.getCAE(), "El CAE no coincide");
        verify(soapConnection).close();
    }

    @Test
    void llamarFECompConsultar_Error() throws Exception {
        // Arrange
        int puntoVenta = 1;
        int tipoComprobante = 1;
        int numeroComprobante = 123;
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<FECompConsultarResponse xmlns=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<FECompConsultarResult>" +
                "<Resultado>R</Resultado>" +
                "<Errors>" +
                "<Err>" +
                "<Code>1001</Code>" +
                "<Msg>Error de validacion</Msg>" +
                "</Err>" +
                "</Errors>" +
                "</FECompConsultarResult>" +
                "</FECompConsultarResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);
        
        // Configurar el mock de soapConnection
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        doReturn(soapMessage).when(soapConnection).call(any(SOAPMessage.class), any(URL.class));

        // Act
        ComprobanteAfip result = client.llamarFECompConsultar(puntoVenta, tipoComprobante, numeroComprobante);

        // Assert
        assertNull(result, "El resultado debería ser null en caso de error");
        verify(soapConnection).close();
    }

    private ComprobanteRequest createTestComprobanteRequest() {
        ComprobanteRequest request = new ComprobanteRequest();
        request.setToken(TOKEN);
        request.setSign(SIGN);
        request.setCuit(CUIT);
        request.setPtoVta(1);
        request.setCantReg(1);
        request.setCtoVta(1);
        request.setCbteTipo(1);
        request.setConcepto(1);
        request.setDocTipo(80);
        request.setDocNro(12345678L);
        request.setCondicionIVAReceptorId(1);
        request.setCbteDesde(1);
        request.setCbteHasta(1);
        request.setCbteFch("20240315");
        request.setImpTotal(100.00);
        request.setImpTotConc(0.00);
        request.setImpNeto(82.64);
        request.setImpOpEx(0.00);
        request.setImpIVA(17.36);
        request.setImpTrib(0.00);
        request.setFchServDesde("20240315");
        request.setFchServHasta("20240315");
        request.setFchVtoPago("20240315");
        request.setMonId("PES");
        request.setMonCotiz(1);
        request.setIdTributo(99);
        request.setDescTributo("IVA");
        request.setBaseImpTributo(82.64);
        request.setAlicTributo(21.00);
        request.setImporteTributo(17.36);
        request.setIdIva(5);
        request.setBaseImp(82.64);
        request.setImporteIva(17.36);
        request.setTributos("IVA");
        request.setIva("21");
        request.setDatosFact("Factura A");
        return request;
    }

    private SOAPMessage createSoapMessage(String xmlContent) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        message.getSOAPPart().setContent(new javax.xml.transform.stream.StreamSource(
            new java.io.StringReader(xmlContent)
        ));
        return message;
    }
} 