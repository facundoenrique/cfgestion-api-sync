package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Table(name = "preventas_articulos")
@Getter
@Setter
public class PreventaArticulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long articuloId; //referencia a tabla articulos.
	private String numero;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private BigDecimal importe;
	private BigDecimal iva = BigDecimal.ZERO;
	private Integer defecto = 1;
	private Integer unidadesPorVulto = 1;
	private Integer multiplicador = 1;
	
	@ManyToOne
	@JoinColumn(name = "preventa_id")
	@JsonBackReference
	private Preventa preventa;


}
