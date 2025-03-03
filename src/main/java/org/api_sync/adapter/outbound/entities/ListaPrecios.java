package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "listas_precios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaPrecios {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private LocalDate fechaCreacion;
	
	@Column(nullable = false)
	private LocalDate fechaModificacion;

	@Column(nullable = false)
	private String nombre;
	
	@OneToMany(mappedBy = "listaPrecios", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemListaPrecios> items;

	@ManyToOne
	@JoinColumn(name = "proveedor_id", nullable = false)
	private Proveedor proveedor;
	
}
