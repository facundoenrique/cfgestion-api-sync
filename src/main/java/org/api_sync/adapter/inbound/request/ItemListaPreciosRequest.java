package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemListaPreciosRequest {
	private Long id;
	@NotEmpty(message = "El numero no puede estar vacío")
	private String numero;
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String nombre;
	@NotNull
	private BigDecimal importe;
	private Integer codUnidadMedida;
	private String marca;
	private String descripcion;
	private BigDecimal iva;
}
