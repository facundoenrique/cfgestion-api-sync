package org.api_sync.adapter.inbound.request.preventa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PreventaRequestDTO {

	@NotBlank
	private String nombre;
	
	@NotNull
	private LocalDate fechaInicio;
	
	@NotNull
	private LocalDate fechaFin;
	
	@NotNull
	private Long listaBaseId;
	
	@NotEmpty
	private List<ArticuloSeleccionadoDTO> articulos;

// Getters y setters
}
