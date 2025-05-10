package org.api_sync.adapter.outbound.repository.gestion;

import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByUuid(String uuid);
}
