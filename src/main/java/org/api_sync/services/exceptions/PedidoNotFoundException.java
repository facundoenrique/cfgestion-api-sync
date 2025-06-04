package org.api_sync.services.exceptions;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long pedidoId) {
        super("No se encontró el pedido con ID: " + pedidoId);
    }
} 