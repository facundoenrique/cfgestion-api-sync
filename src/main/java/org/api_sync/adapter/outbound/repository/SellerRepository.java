package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<Vendedor, Long> {
	// Aquí puedes agregar métodos personalizados si es necesario, por ejemplo:
	Vendedor findByEmail(String email); // Método para buscar un vendedor por email
}
