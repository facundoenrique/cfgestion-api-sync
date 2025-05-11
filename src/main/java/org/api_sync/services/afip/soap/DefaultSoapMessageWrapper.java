package org.api_sync.services.afip.soap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultSoapMessageWrapper implements SoapMessageWrapper {
    private final SOAPMessage soapMessage;
    private final Map<String, String> headers = new HashMap<>();

    public DefaultSoapMessageWrapper(SOAPMessage soapMessage) {
        this.soapMessage = soapMessage;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        try {
            soapMessage.writeTo(out);
        } catch (SOAPException e) {
            throw new IOException("Error writing SOAP message", e);
        }
    }

    @Override
    public SOAPMessage getOriginalMessage() {
        return soapMessage;
    }

    @Override
    public void saveChanges() throws SOAPException {
        soapMessage.saveChanges();
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
        soapMessage.getMimeHeaders().addHeader(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }
} 