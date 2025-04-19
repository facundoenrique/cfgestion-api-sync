package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.PreventaArticulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PreventaArticuloRepository extends JpaRepository<PreventaArticulo, Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM PreventaArticulo pa WHERE pa.preventa.id = :preventaId")
	void deleteByPreventaId(@Param("preventaId") Long preventaId);
}
