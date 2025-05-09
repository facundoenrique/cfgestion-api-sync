package org.api_sync.adapter.outbound.repository.gestion;

import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}
