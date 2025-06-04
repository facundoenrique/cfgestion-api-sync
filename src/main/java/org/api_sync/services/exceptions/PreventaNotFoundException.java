package org.api_sync.services.exceptions;

public class PreventaNotFoundException extends RuntimeException {
    public PreventaNotFoundException(Long preventaId) {
        super(String.format("No se encontró la preventa con ID: %d", preventaId));
    }
}
