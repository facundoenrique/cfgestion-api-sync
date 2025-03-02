package org.api_sync.adapter.inbound.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemListaPreciosRequest {
	private Long id;
	private String numero;
	private String nombre;
	private BigDecimal importe;
	private Integer codUnidadMedida;
	private String marca;
	private String descripcion;
	private BigDecimal iva;
}
