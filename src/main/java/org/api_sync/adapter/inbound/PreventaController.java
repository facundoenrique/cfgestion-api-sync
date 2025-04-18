package org.api_sync.adapter.inbound;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.PropuestaRequestDTO;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.adapter.outbound.entities.Preventa;
import org.api_sync.adapter.outbound.entities.PreventaArticulo;
import org.api_sync.services.proposals.PropuestaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.api_sync.adapter.inbound.responses.PreventaResponseDTO.toPreventaResponseDTO;

@RestController
@RequestMapping("/preventas")
@RequiredArgsConstructor
public class PreventaController {

	private final PropuestaService propuestaService;

	@GetMapping
	public ResponseEntity<Page<PreventaResponseDTO>> findAll(
			@RequestParam(required = false, value = "fecha_desde") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate fechaDesde,
			@RequestParam(required = false, value = "fecha_hasta") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate fechaHasta,
			@RequestParam(required = false) Long proveedorId,
			@RequestParam(required = false) String nombre,
			@PageableDefault(size = 10, sort = "fechaFin", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return ResponseEntity.ok(propuestaService.listar(fechaDesde, fechaHasta, proveedorId,
				nombre, pageable));
	}

	@GetMapping("{id}")
	public ResponseEntity<?> findById(@PathVariable Long id, @RequestParam(required = false) String attributes) {
		PreventaResponseDTO dto = propuestaService.getListaPrecio(id);
		
		// Si no se especificaron atributos, devuelvo todo
		if (attributes == null) {
			return ResponseEntity.ok(dto);
		}
		
		// Parseo los atributos pedidos
		Set<String> requestedAttributes = Arrays.stream(attributes.split(","))
				                                  .map(String::trim)
				                                  .collect(Collectors.toSet());
		
		// Creo un mapa dinámico con solo los atributos pedidos
		Map<String, Object> response = new HashMap<>();
		response.put("id", dto.getId());
		response.put("lista_base_id", dto.getListaBaseId());
		
		if (requestedAttributes.contains("nombre")) {
			response.put("nombre", dto.getNombre());
		}
		if (requestedAttributes.contains("articulos")) {
			response.put("articulos", dto.getArticulos());
		}
		response.put("fecha_inicio", dto.getFechaInicio().toString());
		response.put("fecha_fin", dto.getFechaFin().toString());
		
		
//		if (requestedAttributes.contains("proveedor")) {
//			response.put("proveedor", dto.get());
//		}
		
		
		return ResponseEntity.ok(response);

	}
	
	@PostMapping
	public ResponseEntity<PreventaResponseDTO> crearPropuesta(@Valid @RequestBody PropuestaRequestDTO requestDTO) {
		Preventa propuesta = new Preventa();
		propuesta.setNombre(requestDTO.getNombre());
		propuesta.setFechaInicio(requestDTO.getFechaInicio());
		propuesta.setFechaFin(requestDTO.getFechaFin());
		propuesta.setListaBaseId(requestDTO.getListaBaseId());
		
		// Mapear artículos seleccionados
		List<PreventaArticulo> articulos = requestDTO.getArticulos().stream()
				                                    .map(dto -> {
					                                    PreventaArticulo pa = new PreventaArticulo();
					                                    pa.setArticuloId(dto.getArticuloId());
					                                    pa.setPropuesta(propuesta);
					                                    return pa;
				                                    }).collect(Collectors.toList());
		
		propuesta.setArticulos(articulos);
		
		Preventa guardada = propuestaService.guardarPropuesta(propuesta);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(toPreventaResponseDTO(guardada));
	}
}
