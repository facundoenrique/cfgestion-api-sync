package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PropuestaRequestDTO {

	@NotBlank
	private String nombre;
	
	@NotNull
	private Date fechaInicio;
	
	@NotNull
	private Date fechaFin;
	
	@NotNull
	private Long listaBaseId;
	
	@NotEmpty
	private List<ArticuloSeleccionadoDTO> articulos;

// Getters y setters
}
