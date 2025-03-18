package org.api_sync.services.suppliers;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ProveedorRequest;
import org.api_sync.adapter.outbound.entities.Proveedor;
import org.api_sync.adapter.outbound.entities.Vendedor;
import org.api_sync.adapter.outbound.repository.ProveedorRepository;
import org.api_sync.adapter.outbound.repository.SellerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	
	public Page<Proveedor> getAllSuppliers(Pageable pageable) {
		return supplierRepository.findAll(pageable);
	}
	
	public Proveedor getSupplierById(Long id) {
		return supplierRepository.findById(id)
				       .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
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
		Proveedor recovered = supplierRepository.findById(proveedorId).orElseThrow(() -> new RuntimeException(
				"Proveedor no encontrado"));
		recovered.setCuit(proveedorRequest.getCuit());
		recovered.setRazonSocial(proveedorRequest.getRazonSocial());
		recovered.setTelefono(proveedorRequest.getTelefono());
		recovered.setEmail(proveedorRequest.getEmail());
		recovered.setDomicilio(proveedorRequest.getDomicilio());
		return supplierRepository.save(recovered);
	}
	
}
