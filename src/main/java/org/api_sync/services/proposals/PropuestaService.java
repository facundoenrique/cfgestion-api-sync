package org.api_sync.services.proposals;

import static org.api_sync.adapter.inbound.responses.PreventaResponseDTO.toPreventaResponseDTO;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.ArticuloPreventaDTO;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.adapter.outbound.entities.Preventa;
import org.api_sync.adapter.outbound.repository.PreventaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropuestaService {

	private final PreventaRepository preventaRepository;

	public PreventaResponseDTO getListaPrecio(Long id) {
		Preventa propuesta = preventaRepository.findById(id)
				                                .orElseThrow(() -> new RuntimeException("Preventa no encontrada"));
							
		List<ArticuloPreventaDTO> items = propuesta.getArticulos().stream().map(
				a -> ArticuloPreventaDTO.builder()
								       .id(a.getArticuloId())
								       .nombre(a.getNombre())
								       .precio(a.getImporte())
								       .iva(a.getIva())
								       .defecto(a.getDefecto())
								       .multiplicador(a.getMultiplicador())
								       .unidadesPorBulto(a.getUnidadesPorVulto())
								       .build()
		).toList();
		return toPreventaResponseDTO(propuesta).withArticulos(items);
	}
	
	public Page<PreventaResponseDTO> listar(LocalDate fechaDesde,
	                                        LocalDate fechaHasta,
	                                        Long proveedorId,
	                                        String nombre,
	                                        Pageable pageable) {
		Specification<Preventa> spec = Specification.where(null);
		
		if (fechaDesde != null) {
			spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaFin"), fechaDesde));
		}
		if (fechaHasta != null) {
			spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaFin"), fechaHasta));
		}
		if (proveedorId != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("proveedor").get("id"), proveedorId));
		}
		if (StringUtils.isNotBlank(nombre)) {
			spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
		}
		
		Page<Preventa> propuestaPage = preventaRepository.findAll(spec, pageable);
		
		return propuestaPage.map(PreventaResponseDTO::toPreventaResponseDTO);
	}
	
	public Preventa guardarPropuesta(Preventa preventa) {
		preventa.setFechaCreacion(LocalDate.now());
		return preventaRepository.save(preventa);
	}
}
