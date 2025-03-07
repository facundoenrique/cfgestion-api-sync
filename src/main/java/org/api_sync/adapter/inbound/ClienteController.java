package org.api_sync.adapter.inbound;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.services.clientes.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

	private final ClienteService clienteService;

	@PostMapping
	public ResponseEntity<Cliente> createCustomer(@RequestBody Cliente customer) {
		return ResponseEntity.ok(clienteService.saveCustomer(customer));
	}
	
	@GetMapping
	public ResponseEntity<List<Cliente>> getAllCustomers() {
		return ResponseEntity.ok(clienteService.getAllCustomers());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Optional<Cliente>> getCustomerById(@PathVariable Integer id) {
		return ResponseEntity.ok(clienteService.getCustomerById(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
		clienteService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}
}
