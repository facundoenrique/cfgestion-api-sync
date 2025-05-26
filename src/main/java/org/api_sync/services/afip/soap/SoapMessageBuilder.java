package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.model.ComprobanteRequest;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
public class SoapMessageBuilder {
    private final String token;
    private final String sign;
    private final String cuit;

    public SoapMessageBuilder(String token, String sign, String cuit) {
        this.token = token;
        this.sign = sign;
        this.cuit = cuit;
    }

    public SOAPMessage createFECAESolicitarMessage(ComprobanteRequest comprobanteRequest) {
        String soapMessageXml = buildFECAESolicitarXml(comprobanteRequest);
        return createSoapMessage(soapMessageXml);
    }

    private String buildFECAESolicitarXml(ComprobanteRequest comprobanteRequest) {
        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">" +
               "<SOAP-ENV:Header/><SOAP-ENV:Body>" +
               "<ar:FECAESolicitar>" +
               "<ar:Auth>" +
               "<ar:Token>" + token + "</ar:Token>" +
               "<ar:Sign>" + sign + "</ar:Sign>" +
               "<ar:Cuit>" + cuit + "</ar:Cuit>" +
               "</ar:Auth>" +
               "<ar:FeCAEReq>" +
               "<ar:FeCabReq>" +
               "<ar:CantReg>1</ar:CantReg>" +
               "<ar:PtoVta>" + comprobanteRequest.getPtoVta() + "</ar:PtoVta>" +
               "<ar:CbteTipo>" + comprobanteRequest.getCbteTipo() + "</ar:CbteTipo>" +
               "</ar:FeCabReq>" +
               "<ar:FeDetReq>" +
               "<ar:FECAEDetRequest>" +
               generateFacturaData(comprobanteRequest) +
               generateCompAsociados(comprobanteRequest) +
               generateTributosData(comprobanteRequest) +
               generateIvaData(comprobanteRequest) +
               "</ar:FECAEDetRequest>" +
               "</ar:FeDetReq>" +
               "</ar:FeCAEReq>" +
               "</ar:FECAESolicitar>" +
               "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
    }

    private String generateFacturaData(ComprobanteRequest comprobanteRequest) {
        return "<ar:Concepto>" + comprobanteRequest.getConcepto() + "</ar:Concepto>" +
               "<ar:DocTipo>" + comprobanteRequest.getDocTipo() + "</ar:DocTipo>" +
               "<ar:DocNro>" + comprobanteRequest.getDocNro() + "</ar:DocNro>" +
               "<ar:CbteDesde>" + comprobanteRequest.getCbteDesde() + "</ar:CbteDesde>" +
               "<ar:CbteHasta>" + comprobanteRequest.getCbteHasta() + "</ar:CbteHasta>" +
               "<ar:CbteFch>" + comprobanteRequest.getCbteFch() + "</ar:CbteFch>" +
               "<ar:ImpTotal>" + comprobanteRequest.getImpTotal() + "</ar:ImpTotal>" +
               "<ar:ImpTotConc>" + comprobanteRequest.getImpTotConc() + "</ar:ImpTotConc>" +
               "<ar:ImpNeto>" + comprobanteRequest.getImpNeto() + "</ar:ImpNeto>" +
               "<ar:ImpOpEx>" + comprobanteRequest.getImpOpEx() + "</ar:ImpOpEx>" +
               "<ar:ImpTrib>" + comprobanteRequest.getImpTrib() + "</ar:ImpTrib>" +
               "<ar:ImpIVA>" + comprobanteRequest.getImpIVA() + "</ar:ImpIVA>" +
               "<ar:FchServDesde></ar:FchServDesde>" +
               "<ar:FchServHasta></ar:FchServHasta>" +
               "<ar:FchVtoPago></ar:FchVtoPago>" +
               "<ar:MonId>PES</ar:MonId>" +
               (comprobanteRequest.getCondicionIVAReceptorId() > 0 ? 
                   "<ar:CondicionIVAReceptorId>" + comprobanteRequest.getCondicionIVAReceptorId() + "</ar:CondicionIVAReceptorId>" : 
                   EMPTY) +
               "<ar:MonCotiz>" + comprobanteRequest.getMonCotiz() + "</ar:MonCotiz>";
    }

    private String generateTributosData(ComprobanteRequest comprobanteRequest) {
        if (comprobanteRequest.getIdTributo() == 0) {
            return EMPTY;
        }
        return "<ar:Tributos>" +
               "<ar:Tributo>" +
               "<ar:Id>" + comprobanteRequest.getIdTributo() + "</ar:Id>" +
               "<ar:Desc>" + comprobanteRequest.getDescTributo() + "</ar:Desc>" +
               "<ar:BaseImp>" + comprobanteRequest.getBaseImpTributo() + "</ar:BaseImp>" +
               "<ar:Importe>" + comprobanteRequest.getImporteTributo() + "</ar:Importe>" +
               "</ar:Tributo>" +
               "</ar:Tributos>";
    }

    private String generateCompAsociados(ComprobanteRequest comprobanteRequest) {
        if (comprobanteRequest.getCompAsociado() == null) {
            return EMPTY;
        }

        int tipo = mapTipoComprobante(comprobanteRequest.getCompAsociado().getTipoComprobante());
        String cbteFecha = comprobanteRequest.getCompAsociado().getFechaComprobante().replaceAll("-", EMPTY);

        return "<ar:CbtesAsoc>" +
               "<ar:CbteAsoc>" +
               "<ar:Tipo>" + tipo + "</ar:Tipo>" +
               "<ar:PtoVta>" + comprobanteRequest.getCompAsociado().getPuntoVenta() + "</ar:PtoVta>" +
               "<ar:Nro>" + comprobanteRequest.getCompAsociado().getNumero() + "</ar:Nro>" +
               "<ar:Cuit>" + cuit + "</ar:Cuit>" +
               "<ar:CbteFch>" + cbteFecha + "</ar:CbteFch>" +
               "</ar:CbteAsoc>" +
               "</ar:CbtesAsoc>";
    }

    private int mapTipoComprobante(int tipoComprobante) {
        switch (tipoComprobante) {
            case 1: return 1;
            case 5: return 6; // factura B de epyme
            case 14: return 11; // factura C
            default: return 0;
        }
    }

    private String generateIvaData(ComprobanteRequest comprobanteRequest) {
        if (comprobanteRequest.getImpNeto() <= 0) {
            return EMPTY;
        }

        StringBuilder ivaBuilder = new StringBuilder("<ar:Iva>");
        
        if (comprobanteRequest.getIdIva() != 0) {
            ivaBuilder.append(generateAlicIva(
                comprobanteRequest.getIdIva(),
                comprobanteRequest.getBaseImp(),
                comprobanteRequest.getImporteIva()
            ));
        }
        
        if (comprobanteRequest.getIdIva2() != 0) {
            ivaBuilder.append(generateAlicIva(
                comprobanteRequest.getIdIva2(),
                comprobanteRequest.getBaseImp2(),
                comprobanteRequest.getImporteIva2()
            ));
        }
        
        if (comprobanteRequest.getIdIva3() != 0) {
            ivaBuilder.append(generateAlicIva(
                comprobanteRequest.getIdIva3(),
                comprobanteRequest.getBaseImp3(),
                comprobanteRequest.getImporteIva3()
            ));
        }
        
        ivaBuilder.append("</ar:Iva>");
        return ivaBuilder.toString();
    }

    private String generateAlicIva(int id, double baseImp, double importe) {
        return "<ar:AlicIva>" +
               "<ar:Id>" + id + "</ar:Id>" +
               "<ar:BaseImp>" + baseImp + "</ar:BaseImp>" +
               "<ar:Importe>" + importe + "</ar:Importe>" +
               "</ar:AlicIva>";
    }

    private SOAPMessage createSoapMessage(String soapMessageXml) {
        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage(
                new MimeHeaders(),
                new ByteArrayInputStream(soapMessageXml.getBytes())
            );
            SOAPPart part = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = part.getEnvelope();
            return soapMessage;
        } catch (SOAPException | IOException e) {
            log.error("Error creating SOAP message", e);
            throw new RuntimeException("Error creating SOAP message", e);
        }
    }
} 