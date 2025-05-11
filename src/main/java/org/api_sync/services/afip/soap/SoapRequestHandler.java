package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.config.AfipServiceConfig;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
public class SoapRequestHandler {
    private final AfipServiceConfig config;

    public SoapRequestHandler(AfipServiceConfig config) {
        this.config = config;
    }

    public SOAPMessage executeSoapRequest(String endpointUrl, String soapAction, SOAPMessage soapMessage) throws SOAPException, IOException {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = null;
        try {
            soapConnection = soapConnectionFactory.createConnection();
            URL endpoint = new URL(endpointUrl);
            return soapConnection.call(soapMessage, endpoint);
        } finally {
            if (soapConnection != null) {
                try {
                    soapConnection.close();
                } catch (SOAPException e) {
                    log.warn("Error closing SOAP connection", e);
                }
            }
        }
    }

    private SOAPMessage createSoapMessage(SoapMessageWrapper wrapper) throws SOAPException {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage message = factory.createMessage();
            
            // Obtener el contenido del wrapper
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wrapper.writeTo(out);
            String content = new String(out.toByteArray());
            
            // Crear el cuerpo del mensaje SOAP
            SOAPBody body = message.getSOAPBody();
            SOAPElement rootElement = body.addChildElement("Envelope", "soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
            SOAPElement bodyElement = rootElement.addChildElement("Body", "soapenv");
            bodyElement.addTextNode(content);
            
            // Agregar los headers
            MimeHeaders headers = message.getMimeHeaders();
            for (String headerName : wrapper.getHeaderNames()) {
                String headerValue = wrapper.getHeader(headerName);
                if (headerValue != null) {
                    headers.addHeader(headerName, headerValue);
                }
            }
            
            message.saveChanges();
            return message;
        } catch (IOException e) {
            throw new SOAPException("Error creating SOAP message", e);
        }
    }

    private URL buildURL(String soapEndpointUrl) throws MalformedURLException {
        return new URL(new URL(soapEndpointUrl),
                EMPTY,
                new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL url) throws IOException {
                        URL target = new URL(url.toString());
                        URLConnection connection = target.openConnection();
                        connection.setConnectTimeout(config.getConnectionTimeout());
                        connection.setReadTimeout(config.getReadTimeout());
                        return connection;
                    }
                });
    }
} 