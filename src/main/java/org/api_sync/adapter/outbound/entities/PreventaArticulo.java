package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "preventas_articulos")
@Getter
@Setter
public class PreventaArticulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long articuloId;
	
	@ManyToOne
	@JoinColumn(name = "propuesta_id")
	private Preventa propuesta;


}
