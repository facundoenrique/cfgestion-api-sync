package org.api_sync.services.articulos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.outbound.entities.Articulo;
import org.api_sync.adapter.outbound.repository.ArticuloRepository;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.articulos.dto.PrecioDTO;
import org.api_sync.services.articulos.mappers.ArticuloMapper;
import org.api_sync.services.exceptions.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticuloService {

	private final ArticuloRepository articuloRepository;
	private final ArticuloMapper articuloMapper;
	private final PrecioService precioService;
	
	public ArticuloDTO get(Long id) {
		Articulo articulo = articuloRepository.findById(id)
				                    .orElseThrow(() -> new RuntimeException("Articulo no encontrado"));
		return articuloMapper.toDTO(articulo);
	}
	
	public ArticuloDTO guardarArticulo(ArticuloRequest articuloRequest) {
		Optional<Articulo> art = articuloRepository.findByNumero(articuloRequest.getNumero());
		
		if (art.isPresent()) {
			Articulo item = updateAtributes(art.get(), articuloRequest);
			return articuloMapper.toDTO(articuloRepository.save(item));
		}
		Articulo articulo = articuloMapper.toEntity(articuloRequest);
		return articuloMapper.toDTO(articuloRepository.save(articulo));
	}
	

	private Articulo updateAtributes(Articulo articulo, ArticuloRequest request) {
		articulo.setNombre(request.getNombre());
		articulo.setNumero(request.getNumero());
		articulo.setEliminado(request.getEliminado());
		return articulo;
	}
	
	public ArticuloDTO actualizarArticulo(Long id, ArticuloRequest articuloRequest) {
		if (!articuloRepository.existsById(id)) {
			throw new RuntimeException("Articulo no encontrado");
		}
		Articulo articulo = articuloMapper.toEntity(articuloRequest);
		articulo.setId(id);
		return update(articulo);
	}
	
	private ArticuloDTO update(Articulo articulo) {
		return articuloMapper.toDTO(articuloRepository.save(articulo));
	}
	
//	public void eliminarArticulo(Long id) {
//		articuloRepository.deleteById(id);
//	}

	public List<ArticuloDTO> listarArticulos() {
		return articuloRepository.findAll().stream()
				       .map(item -> {
					       ArticuloDTO dto = null;
					       try {
						       dto = articuloMapper.toDTO(item);
						       PrecioDTO ultimoPrecioDto = precioService.obtenerPrecioVigente(item.getId());
						       dto.setPrecio(ultimoPrecioDto);
					       } catch (Exception e) {
						       log.error(e.getMessage(), e);
					       }
					       return dto;
				       })
				       .toList();
	}

	public ArticuloDTO getItem(String numero) {
		return articuloRepository.findByNumero(numero)
				       .map(item -> {
					       ArticuloDTO dto = null;
						   try {
							   dto = articuloMapper.toDTO(item);
							   PrecioDTO ultimoPrecioDto = precioService.obtenerPrecioVigente(item.getId());
							   dto.setPrecio(ultimoPrecioDto);
						   } catch (Exception e) {
							   log.error(e.getMessage(), e);
						   }
						   return dto;
				       })
				       .orElseThrow(() -> new ItemNotFoundException("ITEM NOT FOUND: " + numero != null ? numero : "ALL"));
	}
  
}

