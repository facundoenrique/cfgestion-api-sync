package org.api_sync.services.exceptions;

public class PreventaArticuloNotFoundException extends RuntimeException {
    public PreventaArticuloNotFoundException(Long preventaArticuloId) {
        super("No se encontró el artículo de preventa con ID: " + preventaArticuloId);
    }
} 