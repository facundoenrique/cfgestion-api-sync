package org.api_sync.adapter.outbound.entities.gestion;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "empresas")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Empresa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String razonSocial;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private String cuit; //si alguien quiere dos empresas como el dube me va a complicar si le pongo unico al cuit
	private String ingresosBrutos;
	private String domicilio;
	private LocalDate inicioActividades;
	private Integer localidad;
	private Integer provincia;
	private String codigoPostal;
	@Column(nullable = false)
	private Short condicionIva;
	private String telefono;
	private String email;
	private String web;
	@Column(unique = true)
	private String uuid;
}
