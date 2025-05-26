package org.api_sync.adapter.outbound.entities.gestion.comprobantes;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "comprobantes_forma_pago")
@NoArgsConstructor
public class ComprobantesFormaPago {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "comprobante_id", nullable = false)
	private Comprobante comprobante;
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal importe;
	@Column(nullable = false)
	private Integer tipo;
	@Column(nullable = false)
	private int codigoFormaPago; //este es el detalle de la forma de pago, ejemplo tarjetas. DEFAULT 0
}
