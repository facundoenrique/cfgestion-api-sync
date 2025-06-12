package org.api_sync.adapter.inbound.responses;

import lombok.Getter;
import lombok.Setter;
import org.api_sync.adapter.outbound.entities.EstadoParticipacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UsuarioPreventaResponseDTO {
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long listaBaseId;
    private boolean tienePedido;
    private Long pedidoId;
    private EstadoParticipacion estadoParticipacion;
    private BigDecimal montoTotal;
    private Integer unidadesPedidas;
    private List<Map<String, Object>> articulos;
} 