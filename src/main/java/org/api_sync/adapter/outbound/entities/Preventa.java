package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preventas")
@Setter
@Getter
public class Preventa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private LocalDate fechaInicio;
	@Column(nullable = false)
	private LocalDate fechaFin;
	@Column(nullable = false)
	private LocalDate fechaCreacion;
	private Long listaBaseId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoPreventa estado = EstadoPreventa.ABIERTA;
	
	@OneToMany(mappedBy = "preventa", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PreventaArticulo> articulos = new ArrayList<>();

}
