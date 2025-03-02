package org.api_sync.services.lista_precios.mappers;

import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.adapter.outbound.entities.Articulo;
import org.api_sync.adapter.outbound.entities.ItemListaPrecios;
import org.api_sync.adapter.outbound.entities.ListaPrecios;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListaPreciosMapper {
	public ListaPreciosDTO toDTO(ListaPrecios listaDePrecios) {
		return ListaPreciosDTO.builder()
				       .id(listaDePrecios.getId())
				       .fechaCreacion(listaDePrecios.getFechaCreacion())
				       .fechaModificacion(listaDePrecios.getFechaModificacion())
				       .build();
	}
	
	public ListaPrecios toEntity(ListaPreciosRequest listaPreciosRequest) {
		List<ItemListaPrecios> items = listaPreciosRequest.getItems().stream().map(
				item -> ItemListaPrecios.builder()
						        .importe(item.getImporte())
						        .articulo(Articulo.builder()
								                  .id(item.getId())
								                  .numero(item.getNumero())
								                  .nombre(item.getNombre())
								                  .codUnidadMedida(item.getCodUnidadMedida())
								                  .iva(item.getIva())
								                  .build())
						        .build()
		).collect(Collectors.toList());
		
		return ListaPrecios.builder()
				                              .fechaCreacion(LocalDate.now())
				                              .fechaModificacion(LocalDate.now())
				                              .items(items)
				                              .build();
		
	}
}
