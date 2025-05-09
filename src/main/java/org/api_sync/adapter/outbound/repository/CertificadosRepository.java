package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.domain.Origen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificadosRepository extends JpaRepository<Certificado, Long> {
	Optional<Certificado> findTopByCuitAndPuntoVentaAndOrigenOrderByFechaCreadoDesc(String cuit,
	                                                                           Integer puntoVenta,
	                                                                           Origen origen);
	Optional<Certificado> findTopByCuitAndPuntoVentaOrderByFechaCreadoDesc(String cuit, Integer puntoVenta);
}
