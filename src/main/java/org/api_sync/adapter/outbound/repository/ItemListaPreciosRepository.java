package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.ItemListaPrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemListaPreciosRepository extends JpaRepository<ItemListaPrecios, Long> {
	Optional<ItemListaPrecios> findByListaPreciosIdAndArticuloId(Long listaPreciosId, Long articuloId);

}
