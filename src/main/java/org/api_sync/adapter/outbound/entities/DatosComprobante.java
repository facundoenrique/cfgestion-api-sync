package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@Entity
@Table(name = "comprobantes",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"numero", "punto_venta", "sucursal", "empresa"})})
@NoArgsConstructor
public class DatosComprobante {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private int codigo;
	@Column(nullable = false)
	private Long empresa;
	@Column(nullable = false)
	private Long sucursal;
	@Column(nullable = false)
	private int punto_venta;
	@Column(nullable = false)
	private int tipo_comprobante;
	@Column(nullable = false)
	private int numero;
	private String fecha;
	private int estado;
	private int enviado;
	@Column(nullable = false)
	private int cod_cli_prov;
	private String hora;
	private int cod_empleado;
	@Column(nullable = false)
	private BigDecimal importe_neto;
	private String fecha_vto;
	private String fecha_comprobante;
	@Column(nullable = false)
	private BigDecimal importe_total;
	private String fecha_iva;
	private String comentario;
	@Column(nullable = false)
	private BigDecimal importe_val;
	@Column(nullable = false)
	private BigDecimal bonif_recargo;
	private int usuario;
	private int estado_trans;
	private String numero_trans; // null
	private int codigo_barras;
	private int cod_domicilio;
	private int caja; //depende del usuario o empleado
	private double vuelto;
	private long cae;
	private int fe_transaccion; //null
	private int fe_email; //null
	private int lista_precios;
	private double redondeo;
}
