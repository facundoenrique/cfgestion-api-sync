package org.api_sync.services.articulos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.outbound.entities.Articulo;
import org.api_sync.adapter.outbound.entities.ItemListaPrecios;
import org.api_sync.adapter.outbound.entities.Precio;
import org.api_sync.adapter.outbound.repository.ArticuloRepository;
import org.api_sync.adapter.outbound.repository.ItemListaPreciosRepository;
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
	private final ItemListaPreciosRepository itemListaPreciosRepository;
	private final ArticuloRepository articuloRepository;
	private final ArticuloMapper articuloMapper;
	private final PrecioService precioService;
	
	public ArticuloDTO get(Long id) {
		Articulo articulo = articuloRepository.findById(id)
				                    .orElseThrow(() -> new RuntimeException("Articulo no encontrado"));
		
		ArticuloDTO dto = articuloMapper.toDTO(articulo);
		PrecioDTO precioDTO = precioService.obtenerPrecioVigente(id);
		dto.setPrecio(precioDTO);
		return dto;
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
		
		Articulo articuloRecuperado = articuloRepository.findById(id)
				                              .orElseThrow(() -> new RuntimeException("Articulo no encontrado"));
		
		articuloRecuperado.setNombre(articuloRequest.getNombre());
		articuloRecuperado.setDescripcion(articuloRequest.getDescripcion());
		articuloRecuperado.setIva(articuloRequest.getIva());
		articuloRecuperado.setNumero(articuloRequest.getNumero());

		if (articuloRequest.getItemListId() != null) {
			ItemListaPrecios itemList = itemListaPreciosRepository.findById(articuloRequest.getItemListId())
					                            .orElseThrow(() -> new RuntimeException("Item list no encontrado"));
			
			Precio precio = itemList.getPrecio();
			precio.setImporte(articuloRequest.getPrecio());
			precioService.save(precio);
		}
		
		return update(articuloRecuperado);
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

