package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "propuestas")
@Setter
@Getter
public class Propuesta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String nombre;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFin;
	@Column(nullable = false)
	private LocalDate fechaCreacion;
	private Long listaBaseId;
	
	@OneToMany(mappedBy = "propuesta", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PropuestaArticulo> articulos = new ArrayList<>();

}
