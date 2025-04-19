package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "preventas_articulos")
@Getter
@Setter
public class PreventaArticulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long articuloId;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private BigDecimal importe;
	@Builder.Default
	private BigDecimal iva = BigDecimal.ZERO;
	@Builder.Default
	private Integer defecto = 1;
	@Builder.Default
	private Integer unidadesPorVulto = 1;
	@Builder.Default
	private Integer multiplicador = 1;
	
	@ManyToOne
	@JoinColumn(name = "preventa_id")
	private Preventa preventa;


}
