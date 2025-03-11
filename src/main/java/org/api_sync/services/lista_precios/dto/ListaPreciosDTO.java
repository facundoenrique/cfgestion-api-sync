package org.api_sync.services.lista_precios.dto;

import lombok.Builder;
import lombok.Data;
import org.api_sync.adapter.outbound.entities.Proveedor;

import java.util.List;

@Builder
@Data
public class ListaPreciosDTO {
	private Long id;
	private String nombre;
	private Proveedor proveedor;
	private String fechaCreacion;
	private String fechaModificacion;
	private List<ItemListaPreciosDTO> items;
}
