package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Date;

@Data
public class ClienteRequest {
	private Integer id;
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String nombre;
	@NotEmpty(message = "El apellido no puede estar vacío")
	private String apellido;
	private String domicilio;
	private Integer localidad;
	private Short provincia;
	private String codigoPostal;
	private Integer dni;
	private String telefono;
	private String fax;
	private String telefono2;
	private Short condicionIva;
	private Short enviado;
	private Date fechaIngreso;
	private Date fechaNacimiento;
	private Short listaPrecio;
	private String cuit;
	private Integer sucursal;
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
