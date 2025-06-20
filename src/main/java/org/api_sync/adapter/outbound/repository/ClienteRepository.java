package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    Optional<Cliente> findByRazonSocial(String razonSocial);
}
