package org.api_sync.adapter.inbound.request.preventa;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.api_sync.adapter.outbound.entities.EstadoPreventa;

@Data
public class PreventaEstadoDTO {
    @NotNull(message = "El estado no puede ser nulo")
    private EstadoPreventa estado;
} 