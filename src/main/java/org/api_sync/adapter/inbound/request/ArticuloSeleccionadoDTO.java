package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArticuloSeleccionadoDTO {
	@NotNull
	private Long articuloId;
	@NotBlank
	private String nombre;
	@NotNull
	private BigDecimal importe;
	private BigDecimal iva;
	private Integer multiplicador;
	private Integer defecto;
	private Integer unidadesPorBulto; //este es solo para info
}