package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
	Optional<Authentication> findTopByCuitAndPuntoVentaOrderByExpirationTime(String cuit, Integer puntoVenta);
}
