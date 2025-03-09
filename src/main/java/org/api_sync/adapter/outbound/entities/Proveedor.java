package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "proveedores", uniqueConstraints = {
		@UniqueConstraint(columnNames = "cuit") // Restringe CUIT a valores únicos
})
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
	
	@Column(unique = true, nullable = true, length = 11)
	private String cuit;
	
	private String domicilio;
//	@OneToOne
//	@JoinColumn(name = "provincia_id", nullable = true)
//	private Provincia provincia;
//	@OneToOne
//	@JoinColumn(name = "localidad_id", nullable = true)
//	private Localidad localidad;
	private String codigoPostal;
	
	@Column(unique = true)
	private String email;
	@Column(unique = true)
	private String telefono;
	private String condicionIva;

	@ManyToOne(fetch = FetchType.LAZY) // Relación Many-to-One con Vendedor
	@JoinColumn(name = "vendedor_id") // Nombre de la columna en la tabla proveedor
	private Vendedor vendedor;
	
	@Column(name = "fecha_creado", nullable = false)
	private LocalDate fechaCreado;
	@PrePersist
	public void prePersist() {
		if (fechaCreado == null) {
			fechaCreado = LocalDate.now(); // Asigna la fecha actual si es NULL
		}
	}
}
