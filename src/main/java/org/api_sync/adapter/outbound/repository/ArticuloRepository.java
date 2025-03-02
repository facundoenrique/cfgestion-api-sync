package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
	Optional<Articulo> findByNumero(String numero);
}
