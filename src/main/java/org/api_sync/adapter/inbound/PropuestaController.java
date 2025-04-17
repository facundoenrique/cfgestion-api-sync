package org.api_sync.adapter.inbound;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.PropuestaRequestDTO;
import org.api_sync.adapter.inbound.responses.PropuestaResponseDTO;
import org.api_sync.adapter.outbound.entities.Propuesta;
import org.api_sync.adapter.outbound.entities.PropuestaArticulo;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/preventas")
@RequiredArgsConstructor
public class PropuestaController {

	private final PropuestaService propuestaService;

	@GetMapping
	public ResponseEntity<Page<PropuestaResponseDTO>> findAll(
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
	public ResponseEntity<PropuestaResponseDTO> findById(@PathVariable Long id) {
		Optional<Propuesta> propuesta = propuestaService.getListaPrecio(id);
		if (propuesta.isPresent()) {
			return ResponseEntity.ok(new PropuestaResponseDTO(propuesta.get()));
		}
		return ResponseEntity.noContent().build();
	}
	
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
