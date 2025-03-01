package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {
	Optional<Precio> findTopByArticuloIdOrderByFechaVigenciaDesc(Long articuloId);
}
