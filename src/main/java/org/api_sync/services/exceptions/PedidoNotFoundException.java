package org.api_sync.services.exceptions;

import org.api_sync.adapter.outbound.entities.Preventa;

public class PedidoNotFoundException extends RuntimeException {
    public PedidoNotFoundException(Long pedidoId) {
        super("No se encontró el pedido con ID: " + pedidoId);
    }

    public PedidoNotFoundException(Preventa preventa) {
        super("No se encontró el pedido de la preventa: " + preventa.getId());
    }
} 