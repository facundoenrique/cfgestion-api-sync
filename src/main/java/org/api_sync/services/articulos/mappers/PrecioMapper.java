package org.api_sync.services.articulos.mappers;

import org.api_sync.adapter.outbound.entities.Precio;
import org.api_sync.services.articulos.dto.PrecioDTO;
import org.springframework.stereotype.Component;

@Component
public class PrecioMapper {

	public PrecioDTO toDTO(Precio precio) {
		return PrecioDTO.builder()
				       .id(precio.getId())
				       .articuloId(precio.getArticulo().getId())
				       .importe(precio.getImporte())
				       .iva(precio.getIva())
				       .fechaVigencia(precio.getFechaVigencia())
				       .build();
	}
}
