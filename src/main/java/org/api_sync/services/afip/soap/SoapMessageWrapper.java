package org.api_sync.services.afip.soap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public interface SoapMessageWrapper {
    void writeTo(OutputStream out) throws IOException;
    SOAPMessage getOriginalMessage();
    void saveChanges() throws SOAPException;
    void addHeader(String name, String value);
    String getHeader(String name);
    Set<String> getHeaderNames();
} 