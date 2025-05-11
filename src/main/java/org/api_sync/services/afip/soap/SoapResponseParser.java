package org.api_sync.services.afip.soap;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.CaeDTO;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Slf4j
@NoArgsConstructor
public class SoapResponseParser {

    public CaeDTO parseCaeResponse(SOAPMessage soapResponse) {
        try {
            String responseXml = convertSoapMessageToString(soapResponse);
            return parseCaeResponseXml(responseXml);
        } catch (Exception e) {
            log.error("Error parsing SOAP response", e);
            throw new RuntimeException("Error parsing SOAP response", e);
        }
    }

    public CaeDTO parseCaeResponseXml(String responseXml) {
        try {
            log.debug("SOAP Response XML: {}", responseXml);

            String resultado = extractValue(responseXml, "Resultado");
            log.debug("Resultado extraído: {}", resultado);
            
            if ("A".equals(resultado)) {
                String cae = extractValue(responseXml, "CAE");
                String caeFechaVto = extractValue(responseXml, "CAEFchVto");
                log.debug("CAE extraído: {}", cae);
                log.debug("CAE Fecha Vto extraída: {}", caeFechaVto);
                
                return CaeDTO.builder()
                    .cae(cae)
                    .caeFechaVto(caeFechaVto)
                    .build();
            } else {
                String codeError = extractValue(responseXml, "Code");
                String messageError = extractValue(responseXml, "Msg");
                log.debug("Código de error extraído: {}", codeError);
                log.debug("Mensaje de error extraído: {}", messageError);
                
                return CaeDTO.builder()
                    .codeError(codeError)
                    .messageError(messageError)
                    .build();
            }
        } catch (Exception e) {
            log.error("Error parsing SOAP response", e);
            throw new RuntimeException("Error parsing SOAP response", e);
        }
    }

    private String convertSoapMessageToString(SOAPMessage soapMessage) throws IOException, SOAPException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        return new String(out.toByteArray());
    }

    private String extractValue(String xml, String tagName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            
            // Primero intentar encontrar el elemento directamente
            NodeList nodeList = doc.getElementsByTagName(tagName);
            log.debug("Buscando elemento {} directamente, encontrados: {}", tagName, nodeList.getLength());
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                log.debug("Valor encontrado directamente: {}", value);
                return value;
            }
            
            // Si no se encuentra, buscar dentro de FECAESolicitarResult
            NodeList resultList = doc.getElementsByTagName("FECAESolicitarResult");
            log.debug("Buscando FECAESolicitarResult, encontrados: {}", resultList.getLength());
            if (resultList.getLength() > 0) {
                Node resultNode = resultList.item(0);
                NodeList childNodes = resultNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    if (childNode.getNodeType() == Node.ELEMENT_NODE && 
                        childNode.getLocalName().equals(tagName)) {
                        String value = childNode.getTextContent();
                        log.debug("Valor encontrado en FECAESolicitarResult: {}", value);
                        return value;
                    }
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting value for tag: {}", tagName, e);
            return null;
        }
    }
} 