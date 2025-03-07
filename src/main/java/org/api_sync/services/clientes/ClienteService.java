package org.api_sync.services.clientes;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
	private ClienteRepository clienteRepository;
	
	public Cliente saveCustomer(Cliente customer) {
		return clienteRepository.save(customer);
	}
	
	public List<Cliente> getAllCustomers() {
		return clienteRepository.findAll();
	}
	
	public Optional<Cliente> getCustomerById(Integer id) {
		return clienteRepository.findById(id);
	}
	
	public void deleteCustomer(Integer id) {
		clienteRepository.deleteById(id);
	}
}
