package org.api_sync.services.lista_precios.mappers;

import org.api_sync.adapter.outbound.entities.ItemListaPrecios;
import org.api_sync.services.lista_precios.dto.ItemListaPreciosDTO;
import org.springframework.stereotype.Component;

@Component
public class ItemListaPreciosMapper {
	public ItemListaPreciosDTO toDTO(ItemListaPrecios entity) {
		return ItemListaPreciosDTO.builder()
				       .id(entity.getId())
				       .numero(entity.getArticulo().getNumero())
				       .nombre(entity.getArticulo().getNombre())
				       .importe(entity.getPrecio().getImporte())
				       .iva(entity.getArticulo().getIva())
				       .build();
	}
}
