package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.UsuarioRelacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRelacionRepository extends JpaRepository<UsuarioRelacion, Long> {
    List<UsuarioRelacion> findByUsuarioId(Long usuarioId);
    Optional<UsuarioRelacion> findByUsuarioIdAndTipoRelacionAndEntidadId(
        Long usuarioId, 
        UsuarioRelacion.TipoRelacion tipoRelacion, 
        Long entidadId
    );
    Optional<UsuarioRelacion> findByUsuarioIdAndTipoRelacion(Long usuarioId, UsuarioRelacion.TipoRelacion tipoRelacion);
} 