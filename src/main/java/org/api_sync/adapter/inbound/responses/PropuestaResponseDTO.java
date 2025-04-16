package org.api_sync.adapter.inbound.responses;

import lombok.Data;
import org.api_sync.adapter.outbound.entities.Propuesta;
import org.api_sync.adapter.outbound.entities.PropuestaArticulo;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PropuestaResponseDTO {

	private Long id;
	private String nombre;
	private Date fechaInicio;
	private Date fechaFin;
	private Long listaBaseId;
	private List<Long> articulos;
	
	public PropuestaResponseDTO(Propuesta propuesta) {
		this.id = propuesta.getId();
		this.nombre = propuesta.getNombre();
		this.fechaInicio = propuesta.getFechaInicio();
		this.fechaFin = propuesta.getFechaFin();
		this.listaBaseId = propuesta.getListaBaseId();
		this.articulos = propuesta.getArticulos()
				                 .stream()
				                 .map(PropuestaArticulo::getArticuloId)
				                 .collect(Collectors.toList());
	}

// Getters
}
