package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Propuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropuestaRepository extends JpaRepository<Propuesta, Long> {
}
