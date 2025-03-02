package org.api_sync.services.lista_precios.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class ListaPreciosDTO {
	private Long id;
	private LocalDate fechaCreacion;
	private LocalDate fechaModificacion;
	private List<ItemListaPreciosDTO> items;
}
