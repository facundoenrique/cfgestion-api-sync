package org.api_sync.adapter.inbound.gestion.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ComprobantesDetalle {
    private Integer articulo; //que tan necesario es ?
    private double cantidad;
    private BigDecimal importeNeto;
    private BigDecimal importeBruto;
    private double descuento;
    private String serie;
    private BigDecimal importe_neto_r;
    private String detalle;
    private BigDecimal costo;
    private double impuestoInterno;
    private int compuesto;
    private String numero; //codigo ean del articulo.
    private int tipo;
    private float tasaIva;
    private double iva;
}
