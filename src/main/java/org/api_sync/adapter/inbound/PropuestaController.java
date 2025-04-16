package org.api_sync.adapter.inbound;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.PropuestaRequestDTO;
import org.api_sync.adapter.inbound.responses.PropuestaResponseDTO;
import org.api_sync.adapter.outbound.entities.Propuesta;
import org.api_sync.adapter.outbound.entities.PropuestaArticulo;
import org.api_sync.services.proposals.PropuestaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/preventas")
@RequiredArgsConstructor
public class PropuestaController {

	private final PropuestaService propuestaService;

	@PostMapping
	public ResponseEntity<PropuestaResponseDTO> crearPropuesta(@Valid @RequestBody PropuestaRequestDTO requestDTO) {
		Propuesta propuesta = new Propuesta();
		propuesta.setNombre(requestDTO.getNombre());
		propuesta.setFechaInicio(requestDTO.getFechaInicio());
		propuesta.setFechaFin(requestDTO.getFechaFin());
		propuesta.setListaBaseId(requestDTO.getListaBaseId());
		
		// Mapear artículos seleccionados
		List<PropuestaArticulo> articulos = requestDTO.getArticulos().stream()
				                                    .map(dto -> {
					                                    PropuestaArticulo pa = new PropuestaArticulo();
					                                    pa.setArticuloId(dto.getArticuloId());
					                                    pa.setPropuesta(propuesta);
					                                    return pa;
				                                    }).collect(Collectors.toList());
		
		propuesta.setArticulos(articulos);
		
		Propuesta guardada = propuestaService.guardarPropuesta(propuesta);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(new PropuestaResponseDTO(guardada));
	}
}
