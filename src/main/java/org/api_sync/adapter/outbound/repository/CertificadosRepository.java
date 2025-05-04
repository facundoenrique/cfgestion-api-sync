package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificadosRepository extends JpaRepository<Certificado, Long> {
	Optional<Certificado> findByCuitAndPuntoVenta(String cuit, Integer puntoVenta);
}
