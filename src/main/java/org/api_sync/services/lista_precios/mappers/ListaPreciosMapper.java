package org.api_sync.services.lista_precios.mappers;

import org.api_sync.adapter.outbound.entities.ItemListaPrecios;
import org.api_sync.adapter.outbound.entities.ListaPrecios;
import org.api_sync.services.lista_precios.dto.ItemListaPreciosDTO;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListaPreciosMapper {
	public ListaPreciosDTO toDTO(ListaPrecios listaDePrecios) {
		return ListaPreciosDTO.builder()
				       .id(listaDePrecios.getId())
				       .fechaCreacion(listaDePrecios.getFechaCreacion())
				       .fechaModificacion(listaDePrecios.getFechaModificacion())
				       .items(mapItems(listaDePrecios.getItems()))
				       .nombre(listaDePrecios.getNombre())
				       .proveedor(listaDePrecios.getProveedor())
				       .build();
	}
	
	public List<ItemListaPreciosDTO> mapItems(List<ItemListaPrecios> itemListaPrecios) {
		return itemListaPrecios.stream().map(
				itemListaPrecio -> ItemListaPreciosDTO.builder()
						                   .nombre(itemListaPrecio.getArticulo().getNombre())
						                   .importe(itemListaPrecio.getPrecio().getImporte()) //deberia sumarle el
						                   // iva asi queda flama
						                   .id(itemListaPrecio.getId())
						                   .build()
		).toList();
	}

}
