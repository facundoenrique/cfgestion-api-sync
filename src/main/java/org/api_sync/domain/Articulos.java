package org.api_sync.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Date;

@Entity
@Table(name = "articulos")
@Data
public class Articulos {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_creado; //es una fecha, despues lo tengo que analizar bien para que se usa.
    private int enviado;
    private double precio1;
    private int familia;
    private int tipo;
    private double descuento;
    private float iva;
    private String nombre;
    private String descripcion;
    private int cod_unidad_medida;
    private double costo;
    private int minimo;
    private String numero; // este es el codigo de barras que se ingresa en la facturacion, tiene que ser String
    private int empresa;
    private int no_stock;
    private int moneda;
    private int proveedor;
    private int defecto;//cantidad de venta, cada vez que se pasa el codigo de barras, por lo gral es uno, pero puede haber casos en que no.
    private int eliminado;
    private double gan1;
    private int maximo;
    private int defecto_compra;
    private int comision;
    private int redondeo;
    private int imagen;
    private int subfamilia;
    private int compuesto;
    private String marca;
    private int cod_iva;
    private double cantidad;
}
