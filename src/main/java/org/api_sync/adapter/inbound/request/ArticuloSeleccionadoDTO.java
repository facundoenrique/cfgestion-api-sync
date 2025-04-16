package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticuloSeleccionadoDTO {
	@NotNull
	private Long articuloId;

}