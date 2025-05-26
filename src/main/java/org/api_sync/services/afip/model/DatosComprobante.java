package org.api_sync.services.afip.model;

import lombok.Data;

@Data
public class DatosComprobante {
	private int tipoComprobante;
	private int puntoVenta;
	private int numero;
	private String fechaComprobante;
}
