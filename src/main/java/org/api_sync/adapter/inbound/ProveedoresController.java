package org.api_sync.adapter.inbound;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ProveedorRequest;
import org.api_sync.adapter.outbound.entities.Proveedor;
import org.api_sync.services.suppliers.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedoresController {

	private final SupplierService supplierService;
	
	@PostMapping
	public ResponseEntity<Proveedor> createSupplier(@RequestBody @Valid ProveedorRequest supplier) {
		return ResponseEntity.ok(supplierService.saveSupplier(supplier));
	}
	
	@GetMapping
	public ResponseEntity<Page<Proveedor>> getAllSuppliers(
			@PageableDefault(size = 25, sort = "razonSocial", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Proveedor>> getSupplierById(@PathVariable Long id) {
		return ResponseEntity.ok(supplierService.getSupplierById(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
		supplierService.deleteSupplier(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Proveedor> updateSupplier(@PathVariable Long id,
	                                                @RequestBody @Valid ProveedorRequest proveedorRequest) {
		Proveedor proveedor = supplierService.update(id, proveedorRequest);
		return ResponseEntity.ok(proveedor);
	}
}

