package org.api_sync.services.proposals;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.PropuestaResponseDTO;
import org.api_sync.adapter.outbound.entities.Propuesta;
import org.api_sync.adapter.outbound.repository.PropuestaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropuestaService {

	private final PropuestaRepository propuestaRepository;

	public Optional<Propuesta> getListaPrecio(Long id) {
		Optional<Propuesta> propuesta = propuestaRepository.findById(id);
		if (propuesta.isPresent()) {
			return propuesta;
		}
		return Optional.empty();
	}
	
	public Page<PropuestaResponseDTO> listar(LocalDate fechaDesde,
	                                         LocalDate fechaHasta,
	                                         Long proveedorId,
	                                         String nombre,
	                                         Pageable pageable) {
		Specification<Propuesta> spec = Specification.where(null);
		
		if (fechaDesde != null) {
			spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaDesde));
		}
		if (fechaHasta != null) {
			spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaCreacion"), fechaHasta));
		}
		if (proveedorId != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("proveedor").get("id"), proveedorId));
		}
		if (StringUtils.isNotBlank(nombre)) {
			spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
		}
		
		Page<Propuesta> propuestaPage = propuestaRepository.findAll(spec, pageable);
		
		return propuestaPage.map(PropuestaResponseDTO::new);
	}
	
	public Propuesta guardarPropuesta(Propuesta propuesta) {
		return propuestaRepository.save(propuesta);
	}
}
