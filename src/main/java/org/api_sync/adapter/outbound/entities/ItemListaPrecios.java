package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
	private Articulo articulo;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal importe;
}
