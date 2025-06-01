package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.config.AfipConstants;
import org.api_sync.services.afip.exceptions.AfipServiceException;
import org.api_sync.services.afip.model.AfipResponseDetails;
import org.api_sync.services.afip.model.CaeDTO;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;

@Slf4j
public class SoapResponseHandler {

    public String handleSoapResponse(SOAPMessage response) throws SOAPException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo(out);
            String strMsg = new String(out.toByteArray());
            log.debug("SOAP Response: {}", strMsg);

            Document doc = parseXml(strMsg);
            String error = extractElementValue(doc, AfipConstants.ELEMENT_ERROR);
            
            if (error != null && !error.isEmpty()) {
                log.error("Error en la respuesta: {}", error);
                throw new AfipServiceException("Error en la respuesta del servicio: " + error);
            }

            return strMsg;
        } catch (Exception e) {
            log.error("Error procesando respuesta SOAP", e);
            throw new AfipServiceException("Error procesando respuesta SOAP", e);
        }
    }

    public CaeDTO handleCaeResponse(SOAPMessage response) throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo(out);
            String strMsg = new String(out.toByteArray());
            log.debug("SOAP Response: {}", strMsg);

            Document doc = parseXml(strMsg);
            
            // Verificamos el resultado de la operación
            String resultado = extractElementValue(doc, "Resultado");
            CaeDTO cae = new CaeDTO();
            
            if ("R".equals(resultado)) {
                log.warn("La respuesta indica un resultado rechazado (R)");
                AfipResponseDetails afipResponseDetails = AfipSoapParser.extractErrors(strMsg);
                cae.setAfipResponseDetails(afipResponseDetails);
                return cae;
            }

            // Si el resultado es exitoso, procesamos la respuesta
            cae.setCae(extractElementValue(doc, AfipConstants.ELEMENT_COD_AUTORIZACION));
            cae.setCaeFchVto(extractElementValue(doc, AfipConstants.ELEMENT_FCH_VTO));
            cae.setCbteFch(extractElementValue(doc, AfipConstants.ELEMENT_CBTE_FCH));
            cae.setCbteNro(extractElementValue(doc, AfipConstants.ELEMENT_CBTE_NRO));
            cae.setCbteTipo(extractElementValue(doc, AfipConstants.ELEMENT_CBTE_TIPO));
            cae.setPtoVta(extractElementValue(doc, AfipConstants.ELEMENT_CBTE_PTO_VTA));
            cae.setImpTotal(extractElementValue(doc, AfipConstants.ELEMENT_IMP_TOTAL));
            cae.setImpNeto(extractElementValue(doc, AfipConstants.ELEMENT_IMP_NETO));
            cae.setImpIva(extractElementValue(doc, AfipConstants.ELEMENT_IMP_IVA));
            cae.setImpTrib(extractElementValue(doc, AfipConstants.ELEMENT_IMP_TRIB));
            cae.setFchServicio(extractElementValue(doc, AfipConstants.ELEMENT_FCH_SERVICIO));
            cae.setFchVtoPago(extractElementValue(doc, AfipConstants.ELEMENT_FCH_VTO_PAGO));
            cae.setMonId(extractElementValue(doc, AfipConstants.ELEMENT_MON_ID));
            cae.setMonCotiz(extractElementValue(doc, AfipConstants.ELEMENT_MON_COTIZ));

            return cae;
        } catch (Exception e) {
            log.error("Error al procesar la respuesta SOAP", e);
            throw e;
        }
    }

    private Document parseXml(String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
    }

    private String extractElementValue(Document doc, String elementName) {
        NodeList nodeList = doc.getElementsByTagName(elementName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
} 