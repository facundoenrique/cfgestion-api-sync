package org.api_sync.services.afip;

import org.api_sync.adapter.outbound.entities.Authentication;
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

import javax.xml.soap.*;
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
    private final Authentication authentication = new Authentication(1l, "token", "sign", "cuit", 1, "cuit");

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
        client = new PSOAPClientSAAJ(authentication, config);
        
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
        mockedMessageFactory.when(() -> SoapMessageFactory.createFECAESolicitarMessage(request, authentication)).thenReturn(soapMessage);
        
        when(requestHandler.executeSoapRequest(
            config.getSoapEndpointUrl(),
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
            mockedMessageFactory.verify(() -> SoapMessageFactory.createFECAESolicitarMessage(request, authentication));
            verify(requestHandler).executeSoapRequest(
                config.getSoapEndpointUrl(),
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
        mockedMessageFactory.when(() -> SoapMessageFactory.createFECAESolicitarMessage(request, authentication)).thenReturn(soapMessage);
        
        when(requestHandler.executeSoapRequest(
            config.getSoapEndpointUrl(),
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
            mockedMessageFactory.verify(() -> SoapMessageFactory.createFECAESolicitarMessage(request, authentication));
            verify(requestHandler).executeSoapRequest(
                config.getSoapEndpointUrl(),
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
        mockedMessageFactory.when(() -> SoapMessageFactory.createFECAESolicitarMessage(request, authentication))
            .thenThrow(expectedException);

        try {
            // Act & Assert
            assertThrows(AfipServiceException.class, () -> client.getCae(request));
            
            // Verificar las llamadas a los mocks
            mockedMessageFactory.verify(() -> SoapMessageFactory.createFECAESolicitarMessage(request, authentication));
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
                "<Iva>" +
                "<AlicIva>" +
                "<Id>5</Id>" +
                "<BaseImp>82.64</BaseImp>" +
                "<Importe>17.36</Importe>" +
                "</AlicIva>" +
                "</Iva>" +
                "</FECompConsultarResult>" +
                "</FECompConsultarResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);
        
        // Configurar el mock de soapConnection
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        doReturn(soapMessage).when(soapConnection).call(any(SOAPMessage.class), any(URL.class));

        // Act
        ComprobanteAfip result = client.getComprobante(puntoVenta, tipoComprobante, numeroComprobante);

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
        
        // Validar los datos de IVA
        assertNotNull(result.getIva(), "La lista de IVA no debería ser null");
        assertEquals(1, result.getIva().size(), "Debería haber un ítem de IVA");
        assertEquals(5, result.getIva().get(0).getId(), "El ID de IVA no coincide");
        assertEquals(82.64, result.getIva().get(0).getBaseImp(), "La base imponible de IVA no coincide");
        assertEquals(17.36, result.getIva().get(0).getImporte(), "El importe de IVA no coincide");
        
        verify(soapConnection).close();
    }

    @Test
    void llamarFECompConsultar_ConMultiplesIva_Success() throws Exception {
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
                "<ImpTotal>2034.04</ImpTotal>" +
                "<ImpNeto>17758.36</ImpNeto>" +
                "<ImpIVA>2034.04</ImpIVA>" +
                "<ImpTrib>0.00</ImpTrib>" +
                "<FchProceso>20240315</FchProceso>" +
                "<CodAutorizacion>12345678901234</CodAutorizacion>" +
                "<Iva>" +
                "<AlicIva>" +
                "<Id>4</Id>" +
                "<BaseImp>17647.06</BaseImp>" +
                "<Importe>1852.94</Importe>" +
                "</AlicIva>" +
                "<AlicIva>" +
                "<Id>6</Id>" +
                "<BaseImp>111.3</BaseImp>" +
                "<Importe>181.1</Importe>" +
                "</AlicIva>" +
                "</Iva>" +
                "</FECompConsultarResult>" +
                "</FECompConsultarResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        SOAPMessage soapMessage = createSoapMessage(xmlContent);
        
        // Configurar el mock de soapConnection
        when(soapConnectionFactory.createConnection()).thenReturn(soapConnection);
        doReturn(soapMessage).when(soapConnection).call(any(SOAPMessage.class), any(URL.class));

        // Act
        ComprobanteAfip result = client.getComprobante(puntoVenta, tipoComprobante, numeroComprobante);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(puntoVenta, result.getPunto_venta(), "El punto de venta no coincide");
        assertEquals(numeroComprobante, result.getNroComp(), "El número de comprobante no coincide");
        assertEquals(tipoComprobante, result.getTipoComp(), "El tipo de comprobante no coincide");
        assertEquals("20240315", result.getCbteFch(), "La fecha del comprobante no coincide");
        assertEquals(2034.04, result.getImpTotal(), "El importe total no coincide");
        assertEquals(17758.36, result.getImpNeto(), "El importe neto no coincide");
        assertEquals(2034.04, result.getImpIvas(), "El importe de IVA no coincide");
        assertEquals(0.00, result.getImpTributos(), "El importe de tributos no coincide");
        assertEquals("20240315", result.getFechaProc(), "La fecha de proceso no coincide");
        assertEquals(12345678901234L, result.getCAE(), "El CAE no coincide");
        
        // Validar los datos de IVA - múltiples alícuotas
        assertNotNull(result.getIva(), "La lista de IVA no debería ser null");
        assertEquals(2, result.getIva().size(), "Debería haber dos ítems de IVA");
        
        // Verificar primera alícuota de IVA
        ComprobanteAfipIva primerIva = result.getIva().get(0);
        assertEquals(4, primerIva.getId(), "El ID del primer IVA no coincide");
        assertEquals(17647.06, primerIva.getBaseImp(), "La base imponible del primer IVA no coincide");
        assertEquals(1852.94, primerIva.getImporte(), "El importe del primer IVA no coincide");
        
        // Verificar segunda alícuota de IVA
        ComprobanteAfipIva segundoIva = result.getIva().get(1);
        assertEquals(6, segundoIva.getId(), "El ID del segundo IVA no coincide");
        assertEquals(111.3, segundoIva.getBaseImp(), "La base imponible del segundo IVA no coincide");
        assertEquals(181.1, segundoIva.getImporte(), "El importe del segundo IVA no coincide");
        
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
        ComprobanteAfip result = client.getComprobante(puntoVenta, tipoComprobante, numeroComprobante);

        // Assert
        assertNull(result, "El resultado debería ser null en caso de error");
        verify(soapConnection).close();
    }

    private ComprobanteRequest createTestComprobanteRequest() {
        ComprobanteRequest request = new ComprobanteRequest();
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