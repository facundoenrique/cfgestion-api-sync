package org.api_sync.adapter.inbound.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ArticuloPreventaDTO {
	private Long id;
	private String nombre;
	private BigDecimal precio;
}
