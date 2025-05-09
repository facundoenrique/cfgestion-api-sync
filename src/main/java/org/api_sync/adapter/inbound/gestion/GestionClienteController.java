package org.api_sync.adapter.inbound.gestion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.gestion.request.GestionClienteRequest;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.gestion.GestionCliente;
import org.api_sync.services.gestion.clientes.GestionClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gestion/clientes")
@RequiredArgsConstructor
public class GestionClienteController {

	private final GestionClienteService clienteService;

	@PostMapping
	public ResponseEntity<GestionCliente> createCustomer(@Valid @RequestBody GestionClienteRequest clienteRequest) {
		return ResponseEntity.ok(clienteService.saveCustomer(clienteRequest));
	}
	
	@GetMapping
	public ResponseEntity<Page<GestionCliente>> getAllCustomers(
			@PageableDefault(size = 25, sort = "razonSocial", direction = Sort.Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(clienteService.getAllCustomers(pageable));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponse> getCustomerById(@PathVariable Long id) {
		return ResponseEntity.ok(clienteService.getCustomerById(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		clienteService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<GestionCliente> updateSupplier(@PathVariable Long id,
	                                                @RequestBody @Valid GestionClienteRequest clienteRequest) {
		GestionCliente cliente = clienteService.update(id, clienteRequest);
		return ResponseEntity.ok(cliente);
	}
}
