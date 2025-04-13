package org.api_sync.services.clientes;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ClienteRequest;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.repository.ClienteRepository;
import org.api_sync.services.clientes.mappers.ClienteMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class ClienteService {
	private final ClienteRepository clienteRepository;
	private final ClienteMapper clienteMapper;
	
	public Cliente saveCustomer(ClienteRequest clienteRequest) {
		Cliente customer = clienteMapper.toEntity(clienteRequest);
		return clienteRepository.save(customer);
	}
	
	public Page<Cliente> getAllCustomers(Pageable pageable) {
		return clienteRepository.findAll(pageable);
	}
	
	public ClienteResponse getCustomerById(Long id) {
		return clienteRepository.findById(id)
				       .map(s -> clienteMapper.toResponse(s))
				       .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
	}
	
	public void deleteCustomer(Long id) {
		clienteRepository.deleteById(id);
	}

	public Cliente update(Long clienteId, ClienteRequest clienteRequest) {
		Cliente recovered = clienteRepository.findById(clienteId).orElseThrow(() -> new RuntimeException(
				"Cliente no encontrado"));
		recovered.setCuit(clienteRequest.getCuit());
		recovered.setRazonSocial(clienteRequest.getRazonSocial());
		recovered.setTelefono(clienteRequest.getTelefono());
		recovered.setEmail(clienteRequest.getEmail());
		recovered.setDomicilio(clienteRequest.getDomicilio());
		return clienteRepository.save(recovered);
	}
}
