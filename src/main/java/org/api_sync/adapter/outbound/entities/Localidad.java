package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "localidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Localidad {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	@OneToOne
	@JoinColumn(name = "provincia_id", nullable = false)
	private Provincia provincia;
}
