package org.api_sync.adapter.outbound.entities.gestion.comprobantes;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Esto deberia ser global, no por empresa.
 * Asi que los voy a agregar a mano al inicio y luego veremos.
 */
@Entity
@Table(name = "impuestos")
public class Impuesto {
	@Id
	@Column(nullable = false)
	private short codigo; //En algun momento se puede llegar a eliminar

	@Column(length = 50)
	private String nombre;

	@Column(precision = 8, scale = 4) // real ? BigDecimal
	private BigDecimal alicuota;
	
	private Short tipoIva;
	
	private Double importeNeto;
	
	private Double importeBruto;
	
	private Short tipo;
	
	private Short recibo;
	
	private Short porCliente;
	
	private Integer provincia;
}
