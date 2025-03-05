package org.api_sync.adapter.inbound.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListaPreciosRequest {
	private List<ItemListaPreciosRequest> items;
	private Long proveedor;
	private String nombre;
}
