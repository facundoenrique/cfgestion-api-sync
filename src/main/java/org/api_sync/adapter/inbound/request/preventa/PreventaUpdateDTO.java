package org.api_sync.adapter.inbound.request.preventa;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
}
