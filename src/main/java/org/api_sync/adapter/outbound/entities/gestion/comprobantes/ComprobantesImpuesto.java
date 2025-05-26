package org.api_sync.adapter.outbound.entities.gestion.comprobantes;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@Entity
@Table(name = "comprobantes_impuestos")
public class ComprobantesImpuesto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_impuesto", nullable = false)
	private Impuesto impuesto;
	@Column(nullable = false)
    private double importe;
	@Column(nullable = false)
    private double neto;
	@ManyToOne
	@JoinColumn(name = "comprobante_id", nullable = false)
	private Comprobante comprobante;
}
