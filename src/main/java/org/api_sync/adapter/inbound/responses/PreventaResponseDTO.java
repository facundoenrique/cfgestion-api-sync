package org.api_sync.adapter.inbound.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.api_sync.adapter.outbound.entities.Preventa;

import java.time.LocalDate;
import java.util.List;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreventaResponseDTO {

	private Long id;
	private String nombre;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaInicio;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaFin;
	private Long listaBaseId;
	@With
	private List<ArticuloPreventaDTO> articulos;
	
	public static PreventaResponseDTO toPreventaResponseDTO(Preventa propuesta) {
		return PreventaResponseDTO.builder()
				       .id(propuesta.getId())
				       .nombre(propuesta.getNombre())
				       .listaBaseId(propuesta.getListaBaseId())
				       .fechaInicio(propuesta.getFechaInicio())
				       .fechaFin(propuesta.getFechaFin())
				       .build();
	}
	
}
