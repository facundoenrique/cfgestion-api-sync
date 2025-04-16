package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "propuestas_articulos")
@Getter
@Setter
public class PropuestaArticulo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long articuloId;
	
	@ManyToOne
	@JoinColumn(name = "propuesta_id")
	private Propuesta propuesta;


}
