package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.api_sync.services.afip.config.AfipConstants;
import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SoapMessageFactory {

    public static SOAPMessage createMessage(String soapAction, String body) throws SOAPException {
        SOAPMessage soapMessage = null;
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage(new MimeHeaders(),
                    new ByteArrayInputStream(
                            body.getBytes()));
            
            // Set SOAP Action header
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);
            soapMessage.saveChanges();
    
            
            
            return soapMessage;
        } catch (Exception e) {
            if (soapMessage!=null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    soapMessage.writeTo(out);
                    log.error("SOAP Message:\n{}", out.toString(StandardCharsets.UTF_8));
                } catch (IOException ex) {
                    log.error(e.getMessage(), e);
                }
                
            }
            log.error("Error creating SOAP message: {}", e.getMessage(), e);
            throw new SOAPException("Error creating SOAP message", e);
        }
    }

    public static SOAPMessage createFECAESolicitarMessage(ComprobanteRequest comprobante,
                                                          Authentication authentication) throws SOAPException {
        
        String requestBody = createFECAESolicitarBody(comprobante, authentication);
        return createMessage(AfipConstants.SOAP_ACTION_FECAE_SOLICITAR, requestBody);
    }

    private static String createFECAESolicitarBody(ComprobanteRequest comprobante, Authentication authentication) {
        return
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">"
                        + "<SOAP-ENV:Header/>"
                            + "<SOAP-ENV:Body>"
                            + "<ar:FECAESolicitar>"
                            + "<ar:Auth>"
                                + "<ar:Token>" + authentication.getToken() + "</ar:Token>"
                                + "<ar:Sign>" + authentication.getSign() + "</ar:Sign>"
                                + "<ar:Cuit>" + authentication.getCuit() + "</ar:Cuit>"
                            + "</ar:Auth>"
                            + "<ar:FeCAEReq>"
                            + "<ar:FeCabReq>"
                            + "<ar:CantReg>1</ar:CantReg>"
                            + "<ar:PtoVta>" + comprobante.getPtoVta() + "</ar:PtoVta>"
                            + "<ar:CbteTipo>" + comprobante.getCbteTipo() + "</ar:CbteTipo>"
                            + "</ar:FeCabReq>"
                            + "<ar:FeDetReq>"
                            + "<ar:FECAEDetRequest>"
                                + generarDatosFact(comprobante)
                                + generarCompAsociados(comprobante, authentication)
                                + generarDatosTributos(comprobante)
                                + generarDatosIva(comprobante)
                            + "</ar:FECAEDetRequest>"
                            + "</ar:FeDetReq>"
                            + "</ar:FeCAEReq>"
                            + "</ar:FECAESolicitar>"
                        + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";
    }

    private static String generarDatosFact(ComprobanteRequest comprobante) {
        String ret =
                "<ar:Concepto>" + comprobante.getConcepto() + "</ar:Concepto>"
                + "<ar:DocTipo>" + comprobante.getDocTipo() + "</ar:DocTipo>"
                + "<ar:DocNro>" + comprobante.getDocNro() + "</ar:DocNro>"
                + "<ar:CbteDesde>" + comprobante.getCbteDesde() + "</ar:CbteDesde>"
                + "<ar:CbteHasta>" + comprobante.getCbteHasta() + "</ar:CbteHasta>"
                + "<ar:CbteFch>" + comprobante.getCbteFch() + "</ar:CbteFch>"
                + "<ar:ImpTotal>" + comprobante.getImpTotal() + "</ar:ImpTotal>"
                + "<ar:ImpTotConc>" + comprobante.getImpTotConc() + "</ar:ImpTotConc>"
                + "<ar:ImpNeto>" + comprobante.getImpNeto() + "</ar:ImpNeto>"
                + "<ar:ImpOpEx>" + comprobante.getImpOpEx() + "</ar:ImpOpEx>"
                + "<ar:ImpTrib>" + comprobante.getImpTrib() + "</ar:ImpTrib>"
                + "<ar:ImpIVA>" + comprobante.getImpIVA() + "</ar:ImpIVA>"
                + "<ar:FchServDesde>" + (comprobante.getFchServDesde() != null ? comprobante.getFchServDesde() : "") + "</ar:FchServDesde>"
                + "<ar:FchServHasta>" + (comprobante.getFchServHasta() != null ? comprobante.getFchServHasta() : "") + "</ar:FchServHasta>"
                + "<ar:FchVtoPago>" + (comprobante.getFchVtoPago() != null ? comprobante.getFchVtoPago() : "") + "</ar:FchVtoPago>"
                + "<ar:MonId>PES</ar:MonId>"
                + (comprobante.getCondicionIVAReceptorId() > 0 ? "<ar:CondicionIVAReceptorId>" + comprobante.getCondicionIVAReceptorId() + "</ar:CondicionIVAReceptorId>" : "")
                + "<ar:MonCotiz>1</ar:MonCotiz>";
        return ret;
    }

    private static String generarCompAsociados(ComprobanteRequest comprobante, Authentication authentication) {
        if (comprobante.getCompAsociado() == null) {
            return StringUtils.EMPTY;
        }
        log.debug("comprobante asociado {}", comprobante.getCompAsociado());

        int tipo = 0;
        if (comprobante.getCompAsociado().getTipoComprobante() == 1) {
            tipo = 1;
        } else if ((comprobante.getCompAsociado().getTipoComprobante() == 5)) { // factura B de epyme
            tipo = 6;// factura b de webservice,
        } else if ((comprobante.getCompAsociado().getTipoComprobante() == 14)) {
            tipo = 11;// factura c, no se si es correcto ese valor
        }

        String cbteFecha = comprobante.getCompAsociado().getFechaComprobante().replaceAll("-", StringUtils.EMPTY);

        String ret =
                "<ar:CbtesAsoc>" +
                    "<ar:CbteAsoc>" +
                    "<ar:Tipo>" + tipo + "</ar:Tipo>" +
                    "<ar:PtoVta>" + comprobante.getCompAsociado().getPuntoVenta() + "</ar:PtoVta>" +
                    "<ar:Nro>" + comprobante.getCompAsociado().getNumero() + "</ar:Nro>" +
                    "<ar:Cuit>" + authentication.getCuit() + "</ar:Cuit>" +
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