package org.api_sync.services.articulos;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Precio;
import org.api_sync.adapter.outbound.repository.PrecioRepository;
import org.api_sync.services.articulos.dto.PrecioDTO;
import org.api_sync.services.articulos.mappers.PrecioMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrecioService {
	private final PrecioRepository precioRepository;
	private final PrecioMapper precioMapper;
	
	public PrecioDTO obtenerPrecioVigente(Long articuloId) {
		return precioRepository.findTopByArticuloIdOrderByFechaVigenciaDesc(articuloId)
				       .map(precioMapper::toDTO)
				       .orElseThrow(() -> new RuntimeException("No hay precios disponibles para este artículo"));
	}
	
	public PrecioDTO save(Precio precio) {
		return precioMapper.toDTO(precioRepository.save(precio));
	}
	
}
