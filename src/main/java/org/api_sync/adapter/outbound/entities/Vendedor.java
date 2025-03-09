package org.api_sync.adapter.outbound.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vendedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendedor {

	@Id
	private Long id;
	
	private String nombre;
	private String apellido;
	private String telefono;
	private String email;
	
	@OneToMany(mappedBy = "vendedor") // Relación One-to-Many con Proveedor
	private List<Proveedor> proveedores; // Lista de proveedores que están asociados con este vendedor


}
