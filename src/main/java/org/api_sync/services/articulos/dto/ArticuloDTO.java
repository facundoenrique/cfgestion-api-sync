package org.api_sync.services.articulos.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ArticuloDTO {

private Long id;
private Date fechaCreado; //es una fecha, despues lo tengo que analizar bien para que se usa.

private int enviado;

private int familia;

private int tipo;

private double descuento;

private float iva;

private String nombre;

private String descripcion;

private int codUnidadMedida;

private double costo;

private int minimo;

private String numero; // este es el codigo de barras que se ingresa en la facturacion, tiene que ser String

private int empresa;

private int noStock;

private int moneda;

private int proveedor;

private int defecto;//cantidad de venta, cada vez que se pasa el codigo de barras, por lo gral es uno, pero puede haber casos en que no.

private int eliminado;

private double gan1;

private int maximo;

private int defectoCompra;

private int comision;

private int redondeo;

private int imagen;

private int subfamilia;

private int compuesto;

private String marca;

private int codIva;

private double cantidad;

private PrecioDTO precio;
}

