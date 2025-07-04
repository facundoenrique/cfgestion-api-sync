package org.api_sync.adapter.outbound.entities;

public enum EstadoParticipacion {
    PENDIENTE, //El usuario no hizo nada
    PARTICIPA, //El usuario inicio el pedido (esta en borrador)
    NO_PARTICIPA, //El usuario marco como no participa
    CONFIRMADO //El usuario confirmo el pedido.
} 