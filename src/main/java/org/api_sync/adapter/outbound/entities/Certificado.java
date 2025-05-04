package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "certificados")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Certificado {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false)
	private byte[] archivo;
	@Column(nullable = false)
	private String password;
	@Column(name = "punto_venta", nullable = false)
	private Integer puntoVenta;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreado;

	@ManyToOne
	@JoinColumn(name = "empresa_id", nullable = false)
	private Cliente cliente;

}
