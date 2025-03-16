package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.ListaPrecios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaPreciosRepository extends JpaRepository<ListaPrecios, Long>, JpaSpecificationExecutor<ListaPrecios> {
}
