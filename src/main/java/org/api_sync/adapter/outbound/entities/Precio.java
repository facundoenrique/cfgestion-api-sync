package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "precios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Precio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "articulo_id", nullable = false)
	private RedArticulo articulo;
	
	@Column(nullable = false)
	private BigDecimal importe;
	
	@Column(name = "fecha_vigencia", nullable = false)
	private LocalDate fechaVigencia;
}
