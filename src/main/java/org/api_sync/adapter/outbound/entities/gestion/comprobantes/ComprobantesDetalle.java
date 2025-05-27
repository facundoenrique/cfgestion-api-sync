package org.api_sync.adapter.outbound.entities.gestion.comprobantes;

import jakarta.persistence.*;
import lombok.*;
import org.api_sync.adapter.outbound.entities.Articulo;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comprobantes_detalles")
@NoArgsConstructor
public class ComprobantesDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo", nullable = false)
    private Articulo articulo;
    @Column(nullable = false)
    private double cantidad;
    @Column(nullable = false)
    private BigDecimal importeNeto;
    @Column(nullable = false)
    private BigDecimal importeBruto;
    private double descuento;
    private String serie;
    @Column(name = "importe_neto_r")
    private BigDecimal importeNetoR;
    private String detalle;
    private BigDecimal costo;
    private double impuestoInterno;
    private int compuesto;
    @Column(nullable = false)
    private String numero; //codigo ean del articulo.
    private int tipo;
    private float tasaIva;
    private double iva;

    @ManyToOne
    @JoinColumn(name = "comprobante_id", nullable = false)
    private Comprobante comprobante;
}
