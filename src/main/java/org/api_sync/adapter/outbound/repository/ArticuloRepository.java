package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.RedArticulo;
import org.api_sync.adapter.outbound.entities.gestion.GestionArticulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticuloRepository extends JpaRepository<RedArticulo, Long>,
		                                            JpaSpecificationExecutor<GestionArticulo> {
	Optional<RedArticulo> findByNumero(String numero);

	Page<RedArticulo> findByNumeroContainingOrNombreContaining(String numero, String nombre, Pageable pageable);
	
}
