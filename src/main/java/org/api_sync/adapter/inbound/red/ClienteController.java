package org.api_sync.adapter.inbound.red;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ClienteRequest;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.services.clientes.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/red/clientes")
@RequiredArgsConstructor
public class ClienteController {

	private final ClienteService clienteService;

	@PostMapping
	public ResponseEntity<Cliente> createCustomer(@RequestBody ClienteRequest clienteRequest) {
		return ResponseEntity.ok(clienteService.saveCustomer(clienteRequest));
	}
	
	@GetMapping
	public ResponseEntity<Page<Cliente>> getAllCustomers(
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
	public ResponseEntity<Cliente> updateSupplier(@PathVariable Long id,
	                                                @RequestBody @Valid ClienteRequest clienteRequest) {
		Cliente cliente = clienteService.update(id, clienteRequest);
		return ResponseEntity.ok(cliente);
	}
}
