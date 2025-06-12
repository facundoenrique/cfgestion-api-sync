package org.api_sync.services.exceptions;

public class PedidoAlreadyExistsException extends RuntimeException {
    public PedidoAlreadyExistsException(Long preventaId, Integer usuarioId) {
        super(String.format("Ya existe un pedido para la preventa %d y el usuario %d", preventaId, usuarioId));
    }
} 