package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.PreventaArticulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreventaArticuloRepository extends JpaRepository<PreventaArticulo, Long> {
	void deleteByPreventaId(Long preVentaId);
}
