package org.api_sync.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtro para capturar y loggear información detallada SOLO de las requests que fallan
 * No loggea requests exitosas para evitar spam en los logs
 */
@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            
            // Wrap request to capture body if needed
            RequestWrapper requestWrapper = new RequestWrapper(httpRequest);
            
            try {
                chain.doFilter(requestWrapper, response);
                
                // Solo loggear si la response indica un error (status >= 400)
                if (httpResponse.getStatus() >= 400) {
                    logRequestInfo(httpRequest, httpResponse, null);
                }
                
            } catch (Exception e) {
                // Loggear información de la request cuando hay una excepción
                logRequestInfo(httpRequest, httpResponse, e);
                throw e;
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private void logRequestInfo(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        try {
            StringBuilder requestInfo = new StringBuilder();
            requestInfo.append("=== FAILED REQUEST DETAILS ===\n");
            requestInfo.append("Method: ").append(request.getMethod()).append("\n");
            requestInfo.append("URL: ").append(request.getRequestURL().toString()).append("\n");
            requestInfo.append("URI: ").append(request.getRequestURI()).append("\n");
            requestInfo.append("Query: ").append(request.getQueryString() != null ? request.getQueryString() : "N/A").append("\n");
            requestInfo.append("Remote: ").append(request.getRemoteAddr()).append(":").append(request.getRemotePort()).append("\n");
            requestInfo.append("User-Agent: ").append(request.getHeader("User-Agent")).append("\n");
            requestInfo.append("Content-Type: ").append(request.getContentType() != null ? request.getContentType() : "N/A").append("\n");
            requestInfo.append("Content-Length: ").append(request.getContentLength()).append("\n");
            
            // Información de la response
            if (response != null) {
                requestInfo.append("Response Status: ").append(response.getStatus()).append("\n");
                requestInfo.append("Response Content Type: ").append(response.getContentType() != null ? response.getContentType() : "N/A").append("\n");
            }
            
            // Log important headers
            requestInfo.append("Headers:\n");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                // Don't log sensitive headers
                if (!isSensitiveHeader(headerName)) {
                    requestInfo.append("  ").append(headerName).append(": ").append(headerValue).append("\n");
                } else {
                    requestInfo.append("  ").append(headerName).append(": [REDACTED]").append("\n");
                }
            }
            
            // Información de la excepción si existe
            if (ex != null) {
                requestInfo.append("Exception: ").append(ex.getClass().getSimpleName()).append("\n");
                requestInfo.append("Exception Message: ").append(ex.getMessage()).append("\n");
            }
            
            requestInfo.append("=== END FAILED REQUEST ===");
            
            log.error("Failed request details: {}", requestInfo.toString());
            
        } catch (Exception e) {
            log.warn("Error logging request info: {}", e.getMessage());
        }
    }

    private boolean isSensitiveHeader(String headerName) {
        String lowerHeader = headerName.toLowerCase();
        return lowerHeader.contains("authorization") || 
               lowerHeader.contains("cookie") || 
               lowerHeader.contains("x-api-key") ||
               lowerHeader.contains("password") ||
               lowerHeader.contains("token");
    }

    /**
     * Wrapper para capturar el body de la request
     */
    private static class RequestWrapper extends HttpServletRequestWrapper {
        private byte[] cachedBody;

        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (cachedBody == null) {
                cacheInputStream();
            }
            return new CachedServletInputStream(cachedBody);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        private void cacheInputStream() throws IOException {
            cachedBody = StreamUtils.copyToByteArray(super.getInputStream());
            
            // Solo loggear body si es JSON o texto (para requests que fallan)
            if (cachedBody.length > 0) {
                String contentType = getContentType();
                if (contentType != null && (contentType.contains("json") || contentType.contains("text"))) {
                    String body = new String(cachedBody, StandardCharsets.UTF_8);
                    log.debug("Request body for failed request: {}", body);
                } else {
                    log.debug("Request body for failed request: [Binary data, {} bytes]", cachedBody.length);
                }
            }
        }
    }

    /**
     * ServletInputStream wrapper para el body cacheado
     */
    private static class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream byteArrayInputStream;

        public CachedServletInputStream(byte[] cachedBody) {
            this.byteArrayInputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return byteArrayInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public int read() throws IOException {
            return byteArrayInputStream.read();
        }
    }
} 