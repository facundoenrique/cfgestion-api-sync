package org.api_sync.services.afip;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.soap.SoapResponseParser;
import org.api_sync.services.afip.exceptions.AfipServiceException;
import org.api_sync.services.afip.config.AfipServiceConfig;
import org.api_sync.services.afip.soap.SoapRequestHandler;
import org.api_sync.services.afip.soap.SoapResponseHandler;
import org.api_sync.services.afip.model.CaeDTO;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.api_sync.services.afip.config.AfipConstants;
import org.api_sync.services.afip.soap.SoapMessageFactory;


@Slf4j
public class PSOAPClientSAAJ {
    private static final String SOAP_ACTION_FECAE_SOLICITAR = "http://ar.gov.afip.dif.FEV1/FECAESolicitar";
    private static final String SOAP_ENDPOINT_URL = "https://servicios1.afip.gov.ar/wsfev1/service.asmx?WSDL";
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private final String token;
    private final String sign;
    private final String cuit;
    private final SoapMessageFactory messageFactory;
    private final SoapRequestHandler requestHandler;
    private final SoapResponseHandler responseHandler;
    private final SoapResponseParser responseParser;
    private final AfipServiceConfig config;
    private String message;

    public PSOAPClientSAAJ(String token, String sign, String cuit, AfipServiceConfig config) {
        this.token = token;
        this.sign = sign;
        this.cuit = cuit;
        this.config = config;
        this.messageFactory = new SoapMessageFactory();
        this.requestHandler = new SoapRequestHandler(config);
        this.responseHandler = new SoapResponseHandler();
        this.responseParser = new SoapResponseParser();
    }

    public CaeDTO getCae(ComprobanteRequest comprobanteRequest) {
        log.info("Ejecutando solicitud de CAE para comprobante: {}", comprobanteRequest);
        try {
            // Construir el mensaje SOAP
            SOAPMessage soapRequest = messageFactory.createFECAESolicitarMessage(comprobanteRequest);
            
            // Ejecutar la petición SOAP
            SOAPMessage soapResponse = requestHandler.executeSoapRequest(
                config.getSoapEndpointUrl(),
                config.getSoapActionFecaeSolicitar(),
                soapRequest
            );
            
            // Procesar la respuesta
            return responseHandler.handleCaeResponse(soapResponse);
        } catch (SOAPException e) {
            log.error("Error SOAP al solicitar CAE", e);
            throw new AfipServiceException("Error en la comunicación SOAP", e);
        } catch (IOException e) {
            log.error("Error de I/O al solicitar CAE", e);
            throw new AfipServiceException("Error en la comunicación con el servicio", e);
        } catch (Exception e) {
            log.error("Error inesperado al solicitar CAE", e);
            throw new AfipServiceException("Error inesperado al solicitar CAE", e);
        }
    }

    public Integer searchUltimaFacturaElectronica(int punto_venta, int tipoComprobante) {
        String soapAction = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";

        return callSoapWebServiceUltimaFE(SOAP_ENDPOINT_URL, soapAction, punto_venta, tipoComprobante);
    }

    private Integer callSoapWebServiceUltimaFE(String soapEndpointUrl, String soapAction, int punto_venta, int tipoComprobante) {
        SOAPConnection soapConnection = null;
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestUltimaFE(soapAction, punto_venta, tipoComprobante), soapEndpointUrl);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            message = strMsg;

            Document doc = parseXml(strMsg);
            String cbteNro = extractElementValue(doc, "CbteNro");
            
            if (cbteNro != null && !cbteNro.equals("0")) {
                log.info("Último comprobante: {}", cbteNro);
            }

            return Integer.parseInt(cbteNro);
        } catch (Exception e) {
            log.error("Error al consultar último comprobante: {}", e.getMessage());
            throw new AfipServiceException("Error al consultar último comprobante: " + e.getMessage(), e);
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    log.error("Error al cerrar la conexión SOAP: {}", e.getMessage());
                }
            }
        }
    }

    private SOAPMessage createSOAPRequestUltimaFE(String soapAction, int punto_venta, int tipoComprobante) throws Exception {
        SOAPMessage soapMessage = createMesaggeUltimaFE(punto_venta, tipoComprobante);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private SOAPMessage createMesaggeUltimaFE(int punto_venta, int tipoComprobante) {
        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"
                + "<FECompUltimoAutorizado xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"
                + "<PtoVta>" + punto_venta + "</PtoVta>"
                + "<CbteTipo>" + tipoComprobante + "</CbteTipo>"
                + "</FECompUltimoAutorizado>"
                + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";

        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return soapMessage;
    }

    public ComprobanteAfip getComprobante(int punto_venta, int cbteTipo, int cbteNro) {
        String soapAction = "http://ar.gov.afip.dif.FEV1/FECompConsultar";
        return callSoapWebServiceFECompConsultar(SOAP_ENDPOINT_URL, soapAction, punto_venta, cbteTipo, cbteNro);
    }

    private ComprobanteAfip callSoapWebServiceFECompConsultar(String soapEndpointUrl, String soapAction, int punto_venta, int cbteTipo, int cbteNro) {
        SOAPConnection soapConnection = null;
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            URL endpoint = buildURL(soapEndpointUrl);
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestFECompConsultar(soapAction, punto_venta, cbteNro, cbteTipo), endpoint);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            message = strMsg;
            log.debug(strMsg);

            Document doc = parseXml(strMsg);
            String error = extractElementValue(doc, "Err");
            
            if (error != null && !error.isEmpty()) {
                log.error("Error en la respuesta: {}", error);
                return null;
            }

            ComprobanteAfip comprobante = new ComprobanteAfip(punto_venta, cbteNro, cbteTipo);
            comprobante.setCbteFch(extractElementValue(doc, "CbteFch"));
            comprobante.setImpTotal(Double.parseDouble(extractElementValue(doc, "ImpTotal")));
            comprobante.setImpNeto(Double.parseDouble(extractElementValue(doc, "ImpNeto")));
            comprobante.setImpIvas(Double.parseDouble(extractElementValue(doc, "ImpIVA")));
            comprobante.setImpTributos(Double.parseDouble(extractElementValue(doc, "ImpTrib")));
            comprobante.setFechaProc(extractElementValue(doc, "FchProceso"));
            comprobante.setCAE(Long.parseLong(extractElementValue(doc, "CodAutorizacion")));

            return comprobante;
        } catch (Exception e) {
            log.error("Error al consultar comprobante: {}", e.getMessage());
            throw new AfipServiceException("Error al consultar comprobante: " + e.getMessage(), e);
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    log.error("Error al cerrar la conexión SOAP: {}", e.getMessage());
                }
            }
        }
    }

    private SOAPMessage createSOAPRequestFECompConsultar(String soapAction, int punto_venta, int nroComp, int cbteTipo) throws Exception {
        SOAPMessage soapMessage = createMesaggeFECompConsultar(punto_venta, nroComp, cbteTipo);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private SOAPMessage createMesaggeFECompConsultar(int punto_venta, int nroComp, int cbteTipo) {
        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"
                + "<FECompConsultar xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"
                + "<ar:FeCompConsReq>"
                + "<ar:CbteTipo>" + cbteTipo + "</ar:CbteTipo>"
                + "<ar:CbteNro>" + nroComp + "</ar:CbteNro>"
                + "<ar:PtoVta>" + punto_venta + "</ar:PtoVta>"
                + "</ar:FeCompConsReq>"
                + "</FECompConsultar>"
                + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";

        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return soapMessage;
    }

    public int llamarFECompUltimoAutorizado(int punto_venta, int cbteTipo) {
        //produccion
        String soapAction = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";
        return callSoapWebServiceCompUltimoAutorizado(SOAP_ENDPOINT_URL, soapAction, punto_venta, cbteTipo);
    }

    private int callSoapWebServiceCompUltimoAutorizado(String soapEndpointUrl, String soapAction, int punto_venta, int cbteTipo) {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapResponse = soapConnection.call(createSOAPRequestCompUltimoAutorizado(soapAction, punto_venta, cbteTipo), soapEndpointUrl);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());

            Document doc = parseXml(strMsg);
            String cbteNro = extractElementValue(doc, "CbteNro");
            log.info("CbteNro: {}", cbteNro);

            soapConnection.close();
            return Integer.parseInt(cbteNro);
        } catch (Exception e) {
            log.error("Error al consultar último comprobante autorizado: {}", e.getMessage(), e);
            throw new AfipServiceException("Error al consultar último comprobante autorizado", e);
        }
    }

    private SOAPMessage createSOAPRequestCompUltimoAutorizado(String soapAction, int punto_venta, int cbteTipo) throws Exception {
        SOAPMessage soapMessage = createMesaggeCompUltimoAutorizado(punto_venta, cbteTipo);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private SOAPMessage createMesaggeCompUltimoAutorizado(int punto_venta, int cbteTipo) {
        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"
                + "<FECompUltimoAutorizado xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"
                + "<PtoVta>" + punto_venta + "</PtoVta>"
                + "<CbteTipo>" + cbteTipo + "</CbteTipo>"
                + "</FECompUltimoAutorizado>"
                + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";

        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            soapMessageWithLeadingComment.getBytes()));
            System.out.println(soapMessage.toString());
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
        } catch (SOAPException | IOException e) {
            log.error(e.getMessage(), e);
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
        }
        return soapMessage;
    }

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private String extractElementValue(Document doc, String elementName) {
        NodeList nodeList = doc.getElementsByTagName(elementName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    private URL buildURL(String soapEndpointUrl) throws MalformedURLException {
        return new URL(new URL(soapEndpointUrl),
                EMPTY,
                new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL url) throws IOException {
                        URL target = new URL(url.toString());
                        URLConnection connection = target.openConnection();
                        connection.setConnectTimeout(CONNECTION_TIMEOUT);
                        connection.setReadTimeout(READ_TIMEOUT);
                        return connection;
                    }
                });
    }

    public String callSoapWebServiceFECompConsultar(String token, String sign, String cuit, String cbteTipo, String cbtePtoVta, String cbteNro) {
        try {
            String authSection = messageFactory.createAuthSection(token, sign, cuit);
            String requestBody = messageFactory.createFECompConsultarBody(authSection, cbteTipo, cbtePtoVta, cbteNro);
            SOAPMessage soapMessage = messageFactory.createMessage(requestBody, AfipConstants.SOAP_ACTION_FE_COMP_CONSULTAR);

            SOAPMessage response = requestHandler.executeSoapRequest(
                AfipConstants.SOAP_ENDPOINT_URL,
                AfipConstants.SOAP_ACTION_FE_COMP_CONSULTAR,
                soapMessage
            );
            return responseHandler.handleSoapResponse(response);

        } catch (Exception e) {
            log.error("Error calling FECompConsultar service", e);
            throw new RuntimeException("Error calling FECompConsultar service: " + e.getMessage(), e);
        }
    }

    public String callSoapWebServiceFECompUltimoAutorizado(String token, String sign, String cuit, String cbteTipo, String cbtePtoVta) {
        try {
            String authSection = messageFactory.createAuthSection(token, sign, cuit);
            String requestBody = messageFactory.createFECompUltimoAutorizadoBody(authSection, cbteTipo, cbtePtoVta);
            SOAPMessage soapMessage = messageFactory.createMessage(requestBody, AfipConstants.SOAP_ACTION_FE_COMP_ULTIMO_AUTORIZADO);

            SOAPMessage response = requestHandler.executeSoapRequest(
                AfipConstants.SOAP_ENDPOINT_URL,
                AfipConstants.SOAP_ACTION_FE_COMP_ULTIMO_AUTORIZADO,
                soapMessage
            );
            return responseHandler.handleSoapResponse(response);

        } catch (Exception e) {
            log.error("Error calling FECompUltimoAutorizado service", e);
            throw new RuntimeException("Error calling FECompUltimoAutorizado service: " + e.getMessage(), e);
        }
    }
}