package org.api_sync.services.articulos;

import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.outbound.entities.Articulo;
import org.api_sync.adapter.outbound.repository.ArticuloRepository;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.articulos.mappers.ArticuloMapper;
import org.api_sync.services.exceptions.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticuloService {

	private final ArticuloRepository articuloRepository;
	private final ArticuloMapper articuloMapper;

	public ArticuloService(ArticuloRepository articuloRepository, ArticuloMapper articuloMapper) {
		this.articuloRepository = articuloRepository;
		this.articuloMapper = articuloMapper;
	}
	
	public ArticuloDTO guardarArticulo(ArticuloRequest articuloRequest) {
		Articulo articulo = articuloMapper.toEntity(articuloRequest);
		return articuloMapper.toDTO(articuloRepository.save(articulo));
	}
	
	public ArticuloDTO actualizarArticulo(Long id, ArticuloRequest articuloRequest) {
		if (!articuloRepository.existsById(id)) {
			throw new RuntimeException("Articulo no encontrado");
		}
		Articulo articulo = articuloMapper.toEntity(articuloRequest);
		articulo.setId(id);
		return articuloMapper.toDTO(articuloRepository.save(articulo));
	}
	
	public void eliminarArticulo(Long id) {
		articuloRepository.deleteById(id);
	}
	
	public List<ArticuloDTO> listarArticulos() {
		return articuloRepository.findAll().stream()
				       .map(articuloMapper::toDTO)
				       .collect(Collectors.toList());
	}

	public ArticuloDTO getItem(String item) {
		return articuloRepository.findByNumero(item)
				       .map(articuloMapper::toDTO)
				       .orElseThrow(() -> new ItemNotFoundException("ITEM NOT FOUND: " + item != null ? item : "ALL"));
	}
  
}

