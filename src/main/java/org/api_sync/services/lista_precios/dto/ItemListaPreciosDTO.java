package org.api_sync.services.lista_precios.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemListaPreciosDTO {
	private Long id;
	private String nombre;
	private BigDecimal importe;
}
