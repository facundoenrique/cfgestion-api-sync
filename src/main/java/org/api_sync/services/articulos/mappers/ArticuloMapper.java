package org.api_sync.services.articulos.mappers;

import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.outbound.entities.Articulo;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.springframework.stereotype.Component;

@Component
public class ArticuloMapper {
	public Articulo toEntity(ArticuloRequest request) {
		Articulo articulo = new Articulo();
		articulo.setNumero(request.getNumero());
		articulo.setNombre(request.getNombre());
		articulo.setDescripcion(request.getDescripcion());
		articulo.setMarca(request.getMarca());
		articulo.setCodUnidadMedida(request.getCodUnidadMedida());
		return articulo;
	}
	
	public ArticuloDTO toDTO(Articulo articulo) {
		ArticuloDTO dto = new ArticuloDTO();
		dto.setId(articulo.getId());
		dto.setNumero(articulo.getNumero());
		dto.setNombre(articulo.getNombre());
		dto.setDescripcion(articulo.getDescripcion());
		dto.setMarca(articulo.getMarca());
		dto.setCodUnidadMedida(articulo.getCodUnidadMedida());
		return dto;
	}
}
