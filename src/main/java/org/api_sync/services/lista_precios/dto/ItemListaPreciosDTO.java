package org.api_sync.services.lista_precios.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemListaPreciosDTO {
	private Long id;
	private String nombre;
	private BigDecimal importe;
}
