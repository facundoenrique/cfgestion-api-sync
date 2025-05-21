package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.api_sync.services.afip.config.AfipConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SoapMessageFactory {
    private static final String SOAP_ENVELOPE_TEMPLATE = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
        "xmlns:ar=\"http://ar.gov.afip.dif.facturaelectronica/\">" +
        "<soapenv:Header/>" +
        "<soapenv:Body>" +
        "%s" +
        "</soapenv:Body>" +
        "</soapenv:Envelope>";

    private static final String AUTH_TEMPLATE = 
        "<ar:Auth>" +
        "<Token>%s</Token>" +
        "<Sign>%s</Sign>" +
        "<Cuit>%s</Cuit>" +
        "</ar:Auth>";

    public static SOAPMessage createMessage(String soapAction, String body) throws SOAPException {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
            
            // Set SOAP Action header
            MimeHeaders headers = message.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);
            
            // Create SOAP envelope
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("ar", "http://ar.gov.afip.dif.facturaelectronica/");
            
            // Set body content
            SOAPBody soapBody = envelope.getBody();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(body.getBytes("UTF-8")));
            Node importedNode = message.getSOAPPart().importNode(document.getDocumentElement(), true);
            soapBody.appendChild(importedNode);
            
            return message;
        } catch (Exception e) {
            log.error("Error creating SOAP message: {}", e.getMessage(), e);
            throw new SOAPException("Error creating SOAP message", e);
        }
    }

    public static String createAuthSection(String token, String sign, String cuit) {
        return String.format(AUTH_TEMPLATE, token, sign, cuit);
    }

    public static String createFECompConsultarBody(String authSection, String cbteTipo, String cbtePtoVta, String cbteNro) {
        return String.format(
            "<ar:FECompConsultarRequest>" +
            "%s" +
            "<ar:FeCAEReq>" +
            "<FeCAEReq>" +
            "<FeCAEReq>" +
            "<CbteTipo>%s</CbteTipo>" +
            "<CbtePtoVta>%s</CbtePtoVta>" +
            "<CbteNro>%s</CbteNro>" +
            "</FeCAEReq>" +
            "</FeCAEReq>" +
            "</ar:FeCAEReq>" +
            "</ar:FECompConsultarRequest>",
            authSection, cbteTipo, cbtePtoVta, cbteNro
        );
    }

    public static String createFECompUltimoAutorizadoBody(String authSection, String cbteTipo, String cbtePtoVta) {
        return String.format(
            "<ar:FECompUltimoAutorizadoRequest>" +
            "%s" +
            "<ar:PtoVta>%s</ar:PtoVta>" +
            "<ar:CbteTipo>%s</ar:CbteTipo>" +
            "</ar:FECompUltimoAutorizadoRequest>",
            authSection, cbtePtoVta, cbteTipo
        );
    }

    public static SOAPMessage createFECAESolicitarMessage(ComprobanteRequest comprobante,
                                                          Authentication authentication) throws SOAPException {
        
        String requestBody = createFECAESolicitarBody(comprobante, authentication);
        return createMessage(AfipConstants.SOAP_ACTION_FECAE_SOLICITAR, requestBody);
    }

    private static String createFECAESolicitarBody(ComprobanteRequest comprobante, Authentication authentication) {
        String soapMessageWithLeadingComment =
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\"><SOAP-ENV:Header/><SOAP-ENV:Body>"
                + "<ar:FECAESolicitar>"
                + "<ar:Auth>"
                + "<ar:Token>" + authentication.getToken() + "</ar:Token>"
                + "<ar:Sign>" + authentication.getSign() + "</ar:Sign>"
                + "<ar:Cuit>" + authentication.getCuit() + "</ar:Cuit>"
                + "</ar:Auth>"
                + "<ar:FeCAEReq>"
                + "<ar:FeCabReq>"
                + "<ar:CantReg>" + comprobante.getCantReg() + "</ar:CantReg>"
                + "<ar:PtoVta>" + comprobante.getPtoVta() + "</ar:PtoVta>"
                + "<ar:CbteTipo>" + comprobante.getCbteTipo() + "</ar:CbteTipo>"
                + "</ar:FeCabReq>"
                + "<ar:FeDetReq>"
                + "<ar:FECAEDetRequest>"
                + generarDatosFact(comprobante)
                + generarCompAsociados(comprobante)
                + generarDatosTributos(comprobante)
                + generarDatosIva(comprobante)
                + "</ar:FECAEDetRequest>"
                + "</ar:FeDetReq>"
                + "</ar:FeCAEReq>"
                + "</ar:FECAESolicitar>"
                + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";

        return soapMessageWithLeadingComment;
    }

    private static String generarDatosFact(ComprobanteRequest comprobante) {
        String ret =
                "<ar:Concepto>" + comprobante.getConcepto() + "</ar:Concepto> "
                + "<ar:DocTipo>" + comprobante.getDocTipo() + "</ar:DocTipo>"
                + "<ar:DocNro>" + comprobante.getDocNro() + "</ar:DocNro>"
                + "<ar:CbteDesde>" + comprobante.getCbteDesde() + "</ar:CbteDesde>"
                + "<ar:CbteHasta>" + comprobante.getCbteHasta() + "</ar:CbteHasta>"
                + "<ar:CbteFch>" + comprobante.getCbteFch() + "</ar:CbteFch>"
                + "<ar:ImpTotal>" + comprobante.getImpTotal() + "</ar:ImpTotal> "
                + "<ar:ImpTotConc>" + comprobante.getImpTotConc() + "</ar:ImpTotConc>"
                + "<ar:ImpNeto>" + comprobante.getImpNeto() + "</ar:ImpNeto>"
                + "<ar:ImpOpEx>" + comprobante.getImpOpEx() + "</ar:ImpOpEx>"
                + "<ar:ImpTrib>" + comprobante.getImpTrib() + "</ar:ImpTrib>"
                + "<ar:ImpIVA>" + comprobante.getImpIVA() + "</ar:ImpIVA>"
                + "<ar:FchServDesde>" + (comprobante.getFchServDesde() != null ? comprobante.getFchServDesde() : "") + "</ar:FchServDesde>"
                + "<ar:FchServHasta>" + (comprobante.getFchServHasta() != null ? comprobante.getFchServHasta() : "") + "</ar:FchServHasta>"
                + "<ar:FchVtoPago>" + (comprobante.getFchVtoPago() != null ? comprobante.getFchVtoPago() : "") + "</ar:FchVtoPago>"
                + "<ar:MonId>" + comprobante.getMonId() + "</ar:MonId>"
                + (comprobante.getCondicionIVAReceptorId() > 0 ? "<ar:CondicionIVAReceptorId>" + comprobante.getCondicionIVAReceptorId() + "</ar:CondicionIVAReceptorId>" : "")
                + "<ar:MonCotiz>" + comprobante.getMonCotiz() + "</ar:MonCotiz>";
        return ret;
    }

    private static String generarCompAsociados(ComprobanteRequest comprobante) {
        if (comprobante.getCompAsociado() == null) {
            return StringUtils.EMPTY;
        }
        log.debug("comprobante asociado {}", comprobante.getCompAsociado());

        int tipo = 0;
        if (comprobante.getCompAsociado().getTipo_comprobante() == 1) {
            tipo = 1;
        } else if ((comprobante.getCompAsociado().getTipo_comprobante() == 5)) { // factura B de epyme
            tipo = 6;// factura b de webservice,
        } else if ((comprobante.getCompAsociado().getTipo_comprobante() == 14)) {
            tipo = 11;// factura c, no se si es correcto ese valor
        }

        String cbteFecha = comprobante.getCompAsociado().getFecha_comprobante().replaceAll("-", StringUtils.EMPTY);

        String ret =
                "<ar:CbtesAsoc>" +
                "<ar:CbteAsoc>" +
                "<ar:Tipo>" + tipo + "</ar:Tipo>" +
                "<ar:PtoVta>" + comprobante.getCompAsociado().getPunto_venta() + "</ar:PtoVta>" +
                "<ar:Nro>" + comprobante.getCompAsociado().getNumero() + "</ar:Nro>" +
                "<ar:Cuit>" + comprobante.getCuit() + "</ar:Cuit>" +
                "<ar:CbteFch>" + cbteFecha + "</ar:CbteFch>" +
                "</ar:CbteAsoc>" +
                "</ar:CbtesAsoc>";
        return ret;
    }

    private static String generarDatosTributos(ComprobanteRequest comprobante) {
        if (comprobante.getIdTributo() == 0) {
            return StringUtils.EMPTY;
        }
        String ret =
                "<ar:Tributos>"
                + "<ar:Tributo>"
                + "<ar:Id>" + comprobante.getIdTributo() + "</ar:Id>"
                + "<ar:Desc>" + comprobante.getDescTributo() + "</ar:Desc>"
                + "<ar:BaseImp>" + comprobante.getBaseImpTributo() + "</ar:BaseImp>"
                // +"<ar:Alic>"+alicTributo+"</ar:Alic>"
                + "<ar:Importe>" + comprobante.getImporteTributo() + "</ar:Importe>"
                + "</ar:Tributo>"
                + "</ar:Tributos>";

        return ret;
    }

    private static String generarDatosIva(ComprobanteRequest comprobante) {
        //que pasa si no tengo que enviar datos de iva ?
        String ret;
        if (comprobante.getImpNeto() > 0) {
            ret = "<ar:Iva>";
            if (comprobante.getIdIva() != 0) {
                ret = ret
                        + "<ar:AlicIva>"
                        + "<ar:Id>" + comprobante.getIdIva() + "</ar:Id>"
                        + "<ar:BaseImp>" + comprobante.getBaseImp() + "</ar:BaseImp>"
                        + "<ar:Importe>" + comprobante.getImporteIva() + "</ar:Importe>"
                        + "</ar:AlicIva>";
            }
            if (comprobante.getIdIva2() != 0) {
                ret = ret
                        + "<ar:AlicIva>"
                        + "<ar:Id>" + comprobante.getIdIva2() + "</ar:Id>"
                        + "<ar:BaseImp>" + comprobante.getBaseImp2() + "</ar:BaseImp>"
                        + "<ar:Importe>" + comprobante.getImporteIva2() + "</ar:Importe>"
                        + "</ar:AlicIva>";
            }
            if (comprobante.getIdIva3() != 0) {
                ret = ret
                        + "<ar:AlicIva>"
                        + "<ar:Id>" + comprobante.getIdIva3() + "</ar:Id>"
                        + "<ar:BaseImp>" + comprobante.getBaseImp3() + "</ar:BaseImp>"
                        + "<ar:Importe>" + comprobante.getImporteIva3() + "</ar:Importe>"
                        + "</ar:AlicIva>";
            }

            ret = ret + "</ar:Iva>";
        } else {
            ret = StringUtils.EMPTY;
        }
        return ret;
    }
} 