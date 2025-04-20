package org.api_sync.services.articulos.mappers;

import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.outbound.entities.Articulo;
import org.api_sync.adapter.outbound.entities.PreventaArticulo;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;

@Component
public class ArticuloMapper {
	public Articulo toEntity(ArticuloRequest request) {
		Articulo articulo = new Articulo();
		articulo.setIva(request.getIva());
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
	
	public Articulo toEntity(PreventaArticulo articulo) {
		return Articulo.builder()
				       .id(articulo.getArticuloId())
				       .numero(articulo.getNumero())
				       .nombre(articulo.getNombre())
				       .iva(articulo.getIva())
				       .cantidad(1)
				       .defecto(1)
				       .eliminado(0)
				       .fechaCreado(Date.from(Instant.now()))
				       .build();
	}
}
