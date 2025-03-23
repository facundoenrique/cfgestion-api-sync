package org.api_sync.services.clientes.mappers;

import org.api_sync.adapter.inbound.request.ClienteRequest;
import org.api_sync.adapter.outbound.entities.Cliente;

public class ClienteMapper {
	public Cliente toEntity(ClienteRequest request) {
		return Cliente.builder()
				       .nombre(request.getNombre())
				       .apellido(request.getApellido())
				       .cuit(request.getCuit())
				       .dni(request.getDni())
				       .email(request.getEmail())
				       .telefono(request.getTelefono())
				       .domicilio(request.getDomicilio())
				       .localidad(request.getLocalidad())
				       .provincia(request.getProvincia())
				       .build();
	}
}
