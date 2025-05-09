package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;
import org.api_sync.domain.Origen;

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
	@Lob
	@Column(nullable = false, columnDefinition = "BLOB")
	private byte[] archivo;
	@Column(nullable = false)
	private String password;
	@Column(name = "punto_venta", nullable = false)
	private Integer puntoVenta;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaCreado;
	@Column(name = "cuit", nullable = false, length = 20)
	private String cuit;
	@Enumerated(EnumType.STRING)
	private Origen origen;
}
