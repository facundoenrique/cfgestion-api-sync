package org.api_sync.adapter.outbound.entities.gestion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
		name = "usuarios",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"empresa_id", "nombre"})
		}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer codigo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	@With
	@JsonIgnore
	private Empresa empresa;
	
	private String nombre;
	private String password;
	@Builder.Default
	private short eliminado = 0;
	//private Long caja; //esto hace referencia a que?
	//private Long empleadoId; //este hace referencia a que ?

}
