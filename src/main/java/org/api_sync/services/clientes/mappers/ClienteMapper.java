package org.api_sync.services.clientes.mappers;

import org.api_sync.adapter.inbound.request.ClienteRequest;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
	public Cliente toEntity(ClienteRequest request) {
		return Cliente.builder()
				       .razonSocial(request.getRazonSocial())
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
