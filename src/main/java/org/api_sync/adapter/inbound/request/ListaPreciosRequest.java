package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ListaPreciosRequest {
	@NotNull
	private List<ItemListaPreciosRequest> items;
	@NotNull
	private Long proveedor;
	@NotNull
	private String nombre;
}
