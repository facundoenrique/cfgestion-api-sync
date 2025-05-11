package org.api_sync.services.afip.exceptions;

public class AfipServiceException extends RuntimeException {
    
    public AfipServiceException(String message) {
        super(message);
    }
    
    public AfipServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 