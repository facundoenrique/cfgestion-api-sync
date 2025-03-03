package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provincias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provincia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;
	private String nombre;
}
