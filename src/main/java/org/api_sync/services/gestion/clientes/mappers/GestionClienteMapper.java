package org.api_sync.services.gestion.clientes.mappers;

import org.api_sync.adapter.inbound.gestion.request.GestionClienteRequest;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.gestion.GestionCliente;
import org.springframework.stereotype.Component;

@Component
public class GestionClienteMapper {
	public GestionCliente toEntity(GestionClienteRequest request) {
		return GestionCliente.builder()
				       .razonSocial(request.getRazonSocial())
				       .cuit(request.getCuit())
				       .dni(request.getDni())
				       .email(request.getEmail())
				       .telefono(request.getTelefono())
				       .domicilio(request.getDomicilio())
				       .localidad(request.getLocalidad())
				       .provincia(request.getProvincia())
				       .condicionIva(request.getCondicionIva())
				       .build();
	}

	public ClienteResponse toResponse(GestionCliente cliente) {
		return ClienteResponse.builder()
				       .razonSocial(cliente.getRazonSocial())
				       .cuit(cliente.getCuit())
				       .email(cliente.getEmail())
				       .telefono(cliente.getTelefono())
				       .domicilio(cliente.getDomicilio())
				       .condicionIva(cliente.getCondicionIva())
				       .build();
	}
}
