package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "clientes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String razonSocial;
	private String domicilio;
	private Integer localidad;
	private Short provincia;
	private String codigoPostal;
	private Integer dni;
	private String telefono;
	private String fax;
	private String telefono2;
	@Column(nullable = false)
	private Short condicionIva;
	private Short enviado;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaIngreso;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaNacimiento;
	
	private Short listaPrecio;
	
	@Column(unique = true)
	private String cuit;
	
	private Integer sucursal;
	
	@Column(unique = true)
	private String email;
	
	private Double saldoCuenta;
	private Short pais;
	private Short empresa;
	private Short tipoDni;
	private String web;
	private Integer empleado;
	private Integer cuentaLimite;
	private Integer transporte;
	private String otros;
	private Short eliminado;
	private Integer campania;
	private String celular;
	private Integer categoria;
	private Integer zona;
	private Float descuento;
	private String imagen;
	private Short verificado;
	private String empleador;
	private Double sueldo;
	private String empleadorCuit;
	private String empleadorDomicilio;
	private Short noVender;
	private Short formaPago;
	private String ingresosBrutos;


}
