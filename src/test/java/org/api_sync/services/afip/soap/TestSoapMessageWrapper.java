package org.api_sync.services.afip.soap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestSoapMessageWrapper implements SoapMessageWrapper {
    private final Map<String, String> headers = new HashMap<>();
    private final String content;
    private boolean changesSaved = false;

    public TestSoapMessageWrapper(String content) {
        this.content = content;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(content.getBytes());
    }

    @Override
    public SOAPMessage getOriginalMessage() {
        throw new UnsupportedOperationException("This is a test wrapper that doesn't use SOAPMessage");
    }

    @Override
    public void saveChanges() throws SOAPException {
        changesSaved = true;
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public boolean isChangesSaved() {
        return changesSaved;
    }

    public String getContent() {
        return content;
    }
} 