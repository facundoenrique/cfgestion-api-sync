package org.api_sync.adapter.inbound.gestion.request;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
public class ComprobantesFormaPago {
	private BigDecimal importe;
	private Integer tipo;
	private int codigoFormaPago; //este es el detalle de la forma de pago, ejemplo tarjetas. DEFAULT 0
}
