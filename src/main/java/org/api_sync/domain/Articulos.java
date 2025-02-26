package org.api_sync.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Date;


@Entity
@Table(name = "articulos")
@Getter
public class Articulos {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreado; //es una fecha, despues lo tengo que analizar bien para que se usa.
	@Column
	private int enviado;
	@Column
	private double precio1;
	@Column
	private int familia;
	@Column
	private int tipo;
	@Column
	private double descuento;
	@Column
	private float iva;
	@Column
	private String nombre;
	@Column
	private String descripcion;
	@Column
	private int codUnidadMedida;
	@Column
	private double costo;
	@Column
	private int minimo;
	@Column
	private String numero; // este es el codigo de barras que se ingresa en la facturacion, tiene que ser String
	@Column
	private int empresa;
	@Column
	private int noStock;
	@Column
	private int moneda;
	@Column
	private int proveedor;
	@Column
	private int defecto;//cantidad de venta, cada vez que se pasa el codigo de barras, por lo gral es uno, pero puede haber casos en que no.
	@Column
	private int eliminado;
	@Column
	private double gan1;
	@Column
	private int maximo;
	@Column
	private int defectoCompra;
	@Column
	private int comision;
	@Column
	private int redondeo;
	@Column
	private int imagen;
	@Column
	private int subfamilia;
	@Column
	private int compuesto;
	@Column
	private String marca;
	@Column
	private int codIva;
	@Column
	private double cantidad;
}
