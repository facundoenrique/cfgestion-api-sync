package org.api_sync.services.suppliers;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ProveedorRequest;
import org.api_sync.adapter.outbound.entities.Proveedor;
import org.api_sync.adapter.outbound.entities.Vendedor;
import org.api_sync.adapter.outbound.repository.ProveedorRepository;
import org.api_sync.adapter.outbound.repository.SellerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {

	private final ProveedorRepository supplierRepository;
	private final SellerRepository vendedorRepository;
	private final SupplierMapper supplierMapper;
	
	public Proveedor saveSupplier(ProveedorRequest supplier) {
		try {
			return supplierRepository.save(supplierMapper.toEntity(supplier));
		} catch (DataIntegrityViolationException e) {
			throw new RuntimeException("El CUIT '" + supplier.getCuit() + "' ya está registrado.");
		}
	}
	
	public List<Proveedor> getAllSuppliers() {
		return supplierRepository.findAll();
	}
	
	public Optional<Proveedor> getSupplierById(Long id) {
		return supplierRepository.findById(id);
	}
	
	public void deleteSupplier(Long id) {
		supplierRepository.deleteById(id);
	}
	
	public Proveedor asociarVendedorAProveedor(Long proveedorId, Long vendedorId) {
		Proveedor proveedor = supplierRepository.findById(proveedorId).orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
		Vendedor vendedor = vendedorRepository.findById(vendedorId).orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
		
		proveedor.setVendedor(vendedor);
		return supplierRepository.save(proveedor);
	}
	
	public Proveedor update(Long proveedorId, ProveedorRequest proveedorRequest) {
		Proveedor entity = supplierMapper.toEntity(proveedorRequest);
		entity.setId(proveedorId);
		return supplierRepository.save(entity);
	}
	
}
