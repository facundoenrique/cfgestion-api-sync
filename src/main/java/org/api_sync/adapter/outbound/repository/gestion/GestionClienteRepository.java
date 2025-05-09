package org.api_sync.adapter.outbound.repository.gestion;

import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.entities.gestion.GestionCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GestionClienteRepository extends JpaRepository<GestionCliente, Long>,
		                                                 JpaSpecificationExecutor<Cliente> {
}
