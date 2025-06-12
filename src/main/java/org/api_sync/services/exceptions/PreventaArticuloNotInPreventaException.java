package org.api_sync.services.exceptions;

public class PreventaArticuloNotInPreventaException extends RuntimeException {
    public PreventaArticuloNotInPreventaException(Long articuloId, Long preventaId) {
        super(String.format("El artículo %d no pertenece a la preventa %d", articuloId, preventaId));
    }
} 