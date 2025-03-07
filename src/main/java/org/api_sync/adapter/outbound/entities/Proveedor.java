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
	
	@Column(unique = true, nullable = false, length = 11)
	private String cuit;
	
	private String domicilio;
	private String localidad;
	private String codigoPostal;
	
	@Column(unique = true)
	private String email;
	
	private String telefono;
	private String condicionIva;
	@Column(name = "fecha_creado", nullable = false)
	private LocalDate fechaCreado;
}
