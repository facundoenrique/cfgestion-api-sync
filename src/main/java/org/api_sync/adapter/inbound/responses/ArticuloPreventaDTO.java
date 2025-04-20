package org.api_sync.adapter.inbound.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ArticuloPreventaDTO {
	private Long id;
	private String numero;
	private String nombre;
	private BigDecimal importe;
	private BigDecimal iva;
	private Integer defecto; //desde cuanto arranco
	private Integer multiplicador; //de a cuanto multiplico
	private Integer unidadesPorBulto;
}
