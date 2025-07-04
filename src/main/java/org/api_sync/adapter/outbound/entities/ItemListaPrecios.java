package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_lista_precios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemListaPrecios {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "lista_de_precios_id", nullable = false)
	private ListaPrecios listaPrecios;
	
	@ManyToOne
	@JoinColumn(name = "articulo_id", nullable = false)
	private RedArticulo articulo;

	@OneToOne
	@JoinColumn(name = "precio_id", nullable = false)
	private Precio precio;

}
