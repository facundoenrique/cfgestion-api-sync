package org.api_sync.adapter.inbound.gestion.request;


import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Comprobante {
	private int codigo;
	private Long sucursal;
	private int puntoVenta;
	private int tipoComprobante;
	private int numero;
	private int estado; //que puedo almacenar aca ?
	private int codEmpleado;
	private BigDecimal importeNeto;
	private BigDecimal importeTotal;
	private Date fecha; //fecha de generacion (guardar junto con hora)
	private Date fechaVto; //fecha de vto que da el caee
	private Date fechaComprobante; //fecha que se le coloca al comprobante
	private Date fechaIva; //fecha_iva, es la fecha en la que se presenta el cae
	private String comentario;
	private BigDecimal bonifRecargo;
	private int usuario;
	private String numeroTrans; // aca va el comprobante asociado, no esta muy practico
	private int codDomicilio;
	private int caja; //depende del usuario o empleado, lo quiero almacenar ?
	private double vuelto; //lo quiero almacenar ? puede ser para reimprimir todo lindo
	private long cae;
	private int listaPrecios;
	private double redondeo;
	private List<ComprobantesImpuesto> impuestos; //esto creo que no va aca
	private List<ComprobantesFormaPago> formaPago;
	private List<ComprobantesDetalle> detalles;
	private Long clienteId;
}
