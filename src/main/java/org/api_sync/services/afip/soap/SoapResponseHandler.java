package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.config.AfipConstants;
import org.api_sync.services.afip.exceptions.AfipServiceException;
import org.api_sync.services.afip.model.AfipResponseDetails;
import org.api_sync.services.afip.model.CaeDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

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

    public CaeDTO handleCaeResponse(SOAPMessage response) throws SOAPException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.writeTo(out);
            String strMsg = new String(out.toByteArray());
            log.debug("SOAP Response: {}", strMsg);

            Document doc = parseXml(strMsg);
            String error = extractElementValue(doc, AfipConstants.ELEMENT_ERROR);
            
            CaeDTO cae = new CaeDTO();
            if (error != null && !error.isEmpty()) {
                log.error("Error en la respuesta: {}", error);
                AfipResponseDetails afipResponseDetails = AfipSoapParser.extractErrors(strMsg);
                cae.setAfipResponseDetails(afipResponseDetails);
                return cae;
            }
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
            log.error("Error procesando respuesta SOAP", e);
            throw new AfipServiceException("Error procesando respuesta SOAP", e);
        }
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
} 