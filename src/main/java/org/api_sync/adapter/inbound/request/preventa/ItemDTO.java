package org.api_sync.adapter.inbound.request.preventa;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDTO {
	private Long id;
	@NotEmpty(message = "El numero no puede estar vacío")
	private String numero;
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String nombre;
	@DecimalMin(value = "0.0", message = "El importe debe ser mayor o igual a 0")
	private BigDecimal importe;
	@DecimalMin(value = "0.0", message = "El importe debe ser mayor o igual a 0")
	private BigDecimal iva;
	@NotNull
	private Integer unidadesPorVulto;
	@NotNull
	private Integer multiplicador;
	private boolean manual;
}
