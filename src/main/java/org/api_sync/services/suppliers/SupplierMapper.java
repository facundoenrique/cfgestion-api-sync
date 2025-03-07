package org.api_sync.services.suppliers;

import org.api_sync.adapter.inbound.request.ProveedorRequest;
import org.api_sync.adapter.outbound.entities.Proveedor;

public class SupplierMapper {
	public Proveedor toEntity(ProveedorRequest request) {
		return Proveedor.builder()
				       .razonSocial(request.getRazonSocial())
				       .cuit(request.getCuit())
				       .domicilio(request.getDomicilio())
				       .build();
	}
}
