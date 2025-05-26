package org.api_sync.adapter.outbound.entities.gestion.comprobantes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.entities.gestion.GestionCliente;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@Entity
@Table(name = "comprobantes",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"numero", "punto_venta", "sucursal", "empresa"})})
@NoArgsConstructor
public class Comprobante {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private int codigo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@With
	@JsonIgnore
	private Empresa empresa;
	@Column(nullable = false)
	private Long sucursal;
	@Column(nullable = false)
	private int puntoVenta;
	@Column(nullable = false)
	private int tipoComprobante;
	@Column(nullable = false)
	private int numero;
	private int estado; //que puedo almacenar aca ?
	private int codEmpleado;
	@Column(nullable = false)
	private BigDecimal importe_neto;
	@Column(nullable = false)
	private BigDecimal importe_total;
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha; //fecha de generacion (guardar junto con hora)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaVto; //fecha de vto que da el caee
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaComprobante; //fecha que se le coloca al comprobante
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaIva; //fecha_iva, es la fecha en la que se presenta el cae
	private String comentario;
	@Column(nullable = false)
	private BigDecimal bonifRecargo;
	private int usuario;
	private String numeroTrans; // aca va el comprobante asociado, no esta muy practico
	private int codDomicilio;
	private int caja; //depende del usuario o empleado, lo quiero almacenar ?
	private double vuelto; //lo quiero almacenar ? puede ser para reimprimir todo lindo
	private long cae;
	private int listaPrecios;
	private double redondeo;
	@OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComprobantesImpuesto> impuestos; //esto creo que no va aca
	@OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComprobantesFormaPago> formaPago;
	@OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ComprobantesDetalle> detalles;
	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false)
	private GestionCliente cliente;
}
