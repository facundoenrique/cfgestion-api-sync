package org.api_sync.adapter.inbound.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {
	private Long id;
	private String razonSocial;
	private String cuit;
	private Short condicionIva;
	private String telefono;
	private String email;
	private String domicilio;
}
