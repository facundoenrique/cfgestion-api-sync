package org.api_sync.adapter.inbound.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import java.util.Date;


@Getter
public class ArticuloRequest {
//	@NotNull(message = "El ID no puede ser nulo") //solo para update y delete
	private Long id;
	private Date fechaCreado; //es una fecha, despues lo tengo que analizar bien para que se usa.
	private int enviado;
	private double precio1;
	private int familia;
	private int tipo;
	private double descuento;
	@DecimalMin(value = "0.0", message = "El IVA debe ser mayor o igual a 0")
	private float iva;
	@NotEmpty(message = "El número no puede estar vacío")
	private String nombre;
	private String descripcion;
	private int codUnidadMedida;
	private double costo;
	private int minimo;
	private String numero; // este es el codigo de barras que se ingresa en la facturacion, tiene que ser String

	private int noStock;

	private int moneda;

	private int proveedor;

	private int defecto;//cantidad de venta, cada vez que se pasa el codigo de barras, por lo gral es uno, pero puede haber casos en que no.

	private int eliminado;

	private double gan1;

	private int maximo;
	
	@Min(value = 0, message = "El defectoCompra debe ser mayor o igual a 0")
	private int defectoCompra;

	private int comision;

	private int redondeo;

	private int imagen;

	private int subfamilia;

	private int compuesto;
	private String marca;
	private int codIva;
	private double cantidad;
}
