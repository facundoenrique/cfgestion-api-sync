package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Preventa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PreventaRepository extends JpaRepository<Preventa, Long>, JpaSpecificationExecutor<Preventa> {
}
