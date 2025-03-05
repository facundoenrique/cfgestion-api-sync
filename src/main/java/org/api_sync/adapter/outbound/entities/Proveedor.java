package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String razonSocial;
	
	@Column(nullable = false)
	private String cuit;
	
	@Column(name = "fecha_creado", nullable = false)
	private LocalDate fechaCreado;
	
	private String domicilio;
	private Integer localidad;
}
