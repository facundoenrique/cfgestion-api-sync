package org.api_sync.services.exceptions;

public class PedidoNotOwnedException extends RuntimeException {
    public PedidoNotOwnedException(Long pedidoId, Long usuarioId) {
        super(String.format("El pedido con ID %d no pertenece al usuario con ID %d", pedidoId, usuarioId));
    }
    public PedidoNotOwnedException(Long pedidoId, Integer usuarioCodigo) {
        super(String.format("El pedido con ID %d no pertenece al usuario con codigo %d", pedidoId, usuarioCodigo));
    }
} 