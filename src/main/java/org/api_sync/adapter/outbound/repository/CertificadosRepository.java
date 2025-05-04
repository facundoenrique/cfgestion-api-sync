package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificadosRepository extends JpaRepository<Certificado, Long> {
	Certificado findByEmpresaAndPuntoVenta(Long empresa, Integer puntoVenta);
}
