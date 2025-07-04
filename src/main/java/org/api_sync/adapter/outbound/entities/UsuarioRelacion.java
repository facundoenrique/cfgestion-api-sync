package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_relaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRelacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "tipo_relacion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoRelacion tipoRelacion;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    public enum TipoRelacion {
        CLIENTE,
        EMPRESA
    }
} 