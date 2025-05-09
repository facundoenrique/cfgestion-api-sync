package org.api_sync.services.gestion.clientes;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.gestion.request.GestionClienteRequest;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.gestion.GestionCliente;
import org.api_sync.adapter.outbound.repository.gestion.GestionClienteRepository;
import org.api_sync.services.gestion.clientes.mappers.GestionClienteMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GestionClienteService {
	private final GestionClienteRepository clienteRepository;
	private final GestionClienteMapper clienteMapper;
	
	public GestionCliente saveCustomer(GestionClienteRequest clienteRequest) {
		GestionCliente customer = clienteMapper.toEntity(clienteRequest);
		return clienteRepository.save(customer);
	}
	
	public Page<GestionCliente> getAllCustomers(Pageable pageable) {
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

	public GestionCliente update(Long clienteId, GestionClienteRequest clienteRequest) {
		GestionCliente recovered = clienteRepository.findById(clienteId).orElseThrow(() -> new RuntimeException(
				"Cliente no encontrado"));
		recovered.setCuit(clienteRequest.getCuit());
		recovered.setRazonSocial(clienteRequest.getRazonSocial());
		recovered.setTelefono(clienteRequest.getTelefono());
		recovered.setEmail(clienteRequest.getEmail());
		recovered.setDomicilio(clienteRequest.getDomicilio());
		return clienteRepository.save(recovered);
	}
}
