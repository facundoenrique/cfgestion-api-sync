package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ListaPreciosUpdateRequest {
	@NotNull private String nombre;
	@NotNull private Long proveedor;
}
