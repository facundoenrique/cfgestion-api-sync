package org.api_sync.services.proposals;

import static org.api_sync.adapter.inbound.responses.PreventaResponseDTO.toPreventaResponseDTO;

import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.inbound.request.preventa.PreventaUpdateDTO;
import org.api_sync.adapter.inbound.responses.ArticuloPreventaDTO;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.adapter.outbound.entities.*;
import org.api_sync.adapter.outbound.repository.*;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.articulos.mappers.ArticuloMapper;
import org.api_sync.services.exceptions.PreventaNotFoundException;
import org.api_sync.services.exceptions.ProveedorNotFoundException;
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
	private final ListaPreciosRepository listaPreciosRepository;
	private final ItemListaPreciosRepository itemListaPreciosRepository;
	private final ArticuloRepository articuloRepository;
	private final ArticuloMapper articuloMapper;
private final PrecioRepository precioRepository;
private final ProveedorRepository proveedorRepository;

public PreventaResponseDTO getListaPrecio(Long id) {
		Preventa propuesta = preventaRepository.findById(id)
				                                .orElseThrow(PreventaNotFoundException::new);
							
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

	public Preventa guardarPreventaManual(Preventa preventa, Long proveedorId) {
	
		Proveedor proveedor = proveedorRepository.findById(proveedorId)
				                   .orElseThrow(ProveedorNotFoundException::new);
	
		preventa.setFechaCreacion(LocalDate.now());
		
		ListaPrecios lista = new ListaPrecios();
		lista.setNombre("Lista asociada a preventa " + preventa.getNombre());
		lista.setFechaCreacion(LocalDate.now());
		lista.setFechaModificacion(LocalDate.now());
		lista.setProveedor(proveedor);
		ListaPrecios listaGuardada = listaPreciosRepository.save(lista);
		
		preventa.getArticulos().forEach(item -> {
			Articulo articulo = articuloRepository.findByNumero(item.getNumero())
					          .orElseGet(() -> articuloRepository.save(articuloMapper.toEntity(item)));
			
			
			Precio precio = Precio.builder()
					                .articulo(articulo)
					                .importe(item.getImporte())
					                .fechaVigencia(LocalDate.now())
					                .build();
			
			Precio precioGuardado = precioRepository.save(precio);
			
			// Crear ItemListaPrecio
			ItemListaPrecios itemLista = new ItemListaPrecios();
			itemLista.setArticulo(articulo);
			itemLista.setPrecio(precioGuardado);
			itemLista.setListaPrecios(listaGuardada);
			itemListaPreciosRepository.save(itemLista);
			
			item.setArticuloId(articulo.getId());
			item.setPreventa(preventa);
			preventa.setListaBaseId(listaGuardada.getId());
		});
		
		//deberia guardar una lista nueva. primero probemos que esto anda correctamente.
		
		return preventaRepository.save(preventa);
	}

	public void actualizarPreVenta(Long id, PreventaUpdateDTO dto) {
		Preventa preVenta = preventaRepository.findById(id)
				               .orElseThrow(PreventaNotFoundException::new);
		
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
		}).toList();
		

		if (preVenta.getListaBaseId() != null && dto.getArticulos().stream().anyMatch(item -> item.getId() == null)) {
			dto.getArticulos().stream().filter(item -> item.getId() == null)
					                         .forEach(itemDTO -> {
												 ArticuloRequest articuloRequest = new ArticuloRequest();
						                         articuloRequest.setNumero(itemDTO.getNumero());
						                         articuloRequest.setNombre(itemDTO.getNombre());
						                         articuloRequest.setPrecio(itemDTO.getImporte());
						                         articuloRequest.setIva(itemDTO.getIva());
						                         articuloRequest.setCantidad(1);
						                         articuloRequest.setEliminado(0);
						                         articuloRequest.setDefecto(itemDTO.getUnidadesPorVulto());
						                         Optional<Articulo> existing =
								                         articuloRepository.findByNumero(itemDTO.getNumero());
												 
						                         if (existing.isPresent()) {
							                         articuloRequest.setId(existing.get().getId());
						                         } else {
							                        Articulo articulo =
									                        articuloRepository.save(articuloMapper.toEntity(articuloRequest));
							                         articuloRequest.setId(articulo.getId());
						                         }
												 
						                         ArticuloDTO articuloDTO =
								                         listaPreciosService.addItem(articuloRequest,
										                         preVenta.getListaBaseId());
						
						                         articulos.stream()
								                         .filter(item -> item.getNumero().equals(articuloDTO.getNumero()))
								                         .forEach(item -> item.setArticuloId(articuloDTO.getId()));
					                         });
			
		}
		
		List<PreventaArticulo> items =
				preventaArticuloRepository.saveAll(articulos.stream().peek(item -> item.setId(null)).toList());
		
		//preVenta.setArticulos(items);
		
		preventaRepository.save(preVenta);
	}
	
}
