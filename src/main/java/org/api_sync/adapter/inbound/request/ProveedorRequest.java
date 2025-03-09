package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProveedorRequest {
	private Long id;
	@NotEmpty(message = "La razón social no puede estar vacía")
	private String razonSocial;
	private String cuit;
	private String domicilio;
	private String telefono;
	private Integer localidad;
	private Integer provincia;
	private Integer codigoPostal;
	private String email;
}

