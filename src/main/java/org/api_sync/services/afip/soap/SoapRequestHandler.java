package org.api_sync.services.afip.soap;

import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.afip.config.AfipServiceConfig;

import javax.xml.soap.*;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class SoapRequestHandler {
    private final AfipServiceConfig config;

    public SoapRequestHandler(AfipServiceConfig config) {
        this.config = config;
    }

    public SOAPMessage executeSoapRequest(String endpointUrl, SOAPMessage soapMessage) throws SOAPException, IOException {
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

} 