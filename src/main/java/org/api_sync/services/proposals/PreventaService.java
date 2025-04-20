package org.api_sync.services.proposals;

import static org.api_sync.adapter.inbound.responses.PreventaResponseDTO.toPreventaResponseDTO;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.inbound.request.preventa.PreventaUpdateDTO;
import org.api_sync.adapter.inbound.responses.ArticuloPreventaDTO;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.adapter.outbound.entities.Preventa;
import org.api_sync.adapter.outbound.entities.PreventaArticulo;
import org.api_sync.adapter.outbound.repository.PreventaArticuloRepository;
import org.api_sync.adapter.outbound.repository.PreventaRepository;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.exceptions.PreventaNotFoundException;
import org.api_sync.services.lista_precios.ListaPreciosService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreventaService {

	private final PreventaRepository preventaRepository;
	private final PreventaArticuloRepository preventaArticuloRepository;
	private final ListaPreciosService listaPreciosService;

	public PreventaResponseDTO getListaPrecio(Long id) {
		Preventa propuesta = preventaRepository.findById(id)
				                                .orElseThrow(() -> new PreventaNotFoundException());
							
		List<ArticuloPreventaDTO> items = propuesta.getArticulos().stream().map(
				a -> ArticuloPreventaDTO.builder()
								       .id(a.getArticuloId())
						                .numero(a.getNumero())
								       .nombre(a.getNombre())
								       .importe(a.getImporte())
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

	public void actualizarPreVenta(Long id, PreventaUpdateDTO dto) {
		Preventa preVenta = preventaRepository.findById(id)
				               .orElseThrow(() -> new PreventaNotFoundException());
		
		preVenta.setNombre(dto.getNombre());
		preVenta.setFechaInicio(dto.getFechaInicio());
		preVenta.setFechaFin(dto.getFechaFin());
		
		// Eliminar articulos actuales
		preventaArticuloRepository.deleteByPreventaId(preVenta.getId());
		
		// Agregar nuevos articulos
		List<PreventaArticulo> articulos = dto.getArticulos().stream().map(item -> {
			PreventaArticulo articulo = new PreventaArticulo();
			articulo.setPreventa(preVenta);
			articulo.setNombre(item.getNombre());
			articulo.setArticuloId(item.getId());
			articulo.setImporte(item.getImporte());
			articulo.setUnidadesPorVulto(item.getUnidadesPorVulto());
			articulo.setMultiplicador(item.getMultiplicador());
			articulo.setNumero(item.getNumero());
			articulo.setIva(item.getIva());
			return articulo;
		}).collect(Collectors.toList());
		

		if (preVenta.getListaBaseId() != null && dto.getArticulos().stream().anyMatch(item -> item.getId() == null)) {
			dto.getArticulos().stream().filter(item -> item.getId() == null)
					                         .forEach(itemDTO -> {
						                         ArticuloRequest articuloRequest = ArticuloRequest.builder()
								                                                           .numero(itemDTO.getNumero())
								                                                           .nombre(itemDTO.getNombre())
								                                                           .precio(itemDTO.getImporte())
								                                                           .iva(itemDTO.getIva())
								                                                           .cantidad(1)
								                                                           .eliminado(0)
								                                                           .defecto(itemDTO.getUnidadesPorVulto())
								                                                           .build();
												 
						                         ArticuloDTO articuloDTO = listaPreciosService.addItem(articuloRequest, preVenta.getListaBaseId());
						
						                         articulos.stream()
								                         .filter(item -> item.getNumero().equals(articuloDTO.getNumero()))
								                         .forEach(item -> item.setId(articuloDTO.getId()));
					                         });
			
		}
		
		List<PreventaArticulo> items =
				preventaArticuloRepository.saveAll(articulos.stream().peek(item -> item.setId(null)).toList());
		
		//preVenta.setArticulos(items);
		
		preventaRepository.save(preVenta);
	}
	
}
