package org.api_sync.services.proposals;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Propuesta;
import org.api_sync.adapter.outbound.repository.PropuestaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropuestaService {

	private final PropuestaRepository propuestaRepository;

	
	public Propuesta guardarPropuesta(Propuesta propuesta) {
		return propuestaRepository.save(propuesta);
	}
}
