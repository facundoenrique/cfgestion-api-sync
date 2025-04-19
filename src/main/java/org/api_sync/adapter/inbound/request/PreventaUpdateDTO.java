package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PreventaUpdateDTO {
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String nombre;
	@NotNull
	private LocalDate fechaInicio;
	@NotNull
	private LocalDate fechaFin;
	private List<ItemDTO> articulos;
	
	// getters y setters
	
	@Data
	public static class ItemDTO {
		private Long id;
		@NotEmpty(message = "El nombre no puede estar vacío")
		private String nombre;
		@DecimalMin(value = "0.0", message = "El importe debe ser mayor o igual a 0")
		private BigDecimal importe;
		@NotNull
		private Integer unidadesPorVulto;
		@NotNull
		private Integer multiplicador;
		private boolean manual;
	}
}
