package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articulos_compuestos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArticulosCompuestos {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String numero;
	private Integer cantidad;
}
