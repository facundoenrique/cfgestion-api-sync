package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
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
	private Long empresa;
	private String nombre;
	private String password;
	private boolean eliminado;
	//private Long caja; //esto hace referencia a que?
	//private Long empleadoId; //este hace referencia a que ?
}
