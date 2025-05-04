package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
	Authentication findByEmpresaAndPuntoVenta(Long empresa, Integer puntoVenta);
}
