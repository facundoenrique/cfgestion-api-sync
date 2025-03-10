package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.ListaPrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaDePreciosRepository extends JpaRepository<ListaPrecios, Long> {
}
