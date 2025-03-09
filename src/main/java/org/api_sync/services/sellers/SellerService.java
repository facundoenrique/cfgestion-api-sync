package org.api_sync.services.sellers;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Vendedor;
import org.api_sync.adapter.outbound.repository.SellerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

	private final SellerRepository vendedorRepository;
	
	// Método para obtener un vendedor por ID
	public Vendedor obtenerVendedorPorId(Long id) {
		return vendedorRepository.findById(id)
				       .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
	}
	
	// Método para guardar un nuevo vendedor
	public Vendedor guardarVendedor(Vendedor vendedor) {
		return vendedorRepository.save(vendedor);
	}
	
	// Método para buscar un vendedor por email
	public Vendedor buscarVendedorPorEmail(String email) {
		return vendedorRepository.findByEmail(email);
	}
}
