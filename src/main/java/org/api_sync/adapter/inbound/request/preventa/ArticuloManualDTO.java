package org.api_sync.adapter.inbound.request.preventa;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArticuloManualDTO {
	@NotEmpty(message = "El número no puede estar vacío")
	private String numero;
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String nombre;
	@NotNull
	private BigDecimal importe;
	private BigDecimal iva;
	private Integer multiplicador;
	private Integer defecto;
	private Integer unidadesPorBulto; //este es solo para info
}