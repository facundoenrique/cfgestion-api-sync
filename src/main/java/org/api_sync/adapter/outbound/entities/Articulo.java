package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "articulos",
		uniqueConstraints = {
		@UniqueConstraint(columnNames = {"numero", "empresa_id"}),
		@UniqueConstraint(columnNames = {"codigo", "empresa_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Articulo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private Integer codigo;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreado; //es una fecha, despues lo tengo que analizar bien para que se usa.
	
	private int enviado;
	
	private int familia;
	
	private int tipo;
	
	private double descuento;

	@Column(nullable = false)
	private BigDecimal iva;
	@Column(nullable = false)
	private String nombre;
	
	private String descripcion;
	
	private int codUnidadMedida;
	
	private int minimo;
	@Column(nullable = false, unique = true)
	private String numero; // este es el codigo de barras que se ingresa en la facturacion, tiene que ser String
	
	private int noStock;
	
	private int moneda;
	
	private int defecto;//cantidad de venta, cada vez que se pasa el codigo de barras, por lo gral es uno, pero puede haber casos en que no.
	
	private int eliminado;
	
	private double gan1;
	
	private int maximo;
	
	private int comision;
	
	private int redondeo;
	
	private int imagen;
	
	private int subfamilia;
	
	private int compuesto;
	
	private String marca;
	
	private double cantidad;
}
