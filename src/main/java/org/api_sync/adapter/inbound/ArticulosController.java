package org.api_sync.adapter.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.services.articulos.ArticuloService;
import org.api_sync.services.articulos.PrecioService;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.articulos.dto.PrecioDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/articulos")
@RequiredArgsConstructor
public class ArticulosController {

	private final ArticuloService articuloService;
	private final PrecioService precioService;

	@GetMapping("/{id}")
	public ResponseEntity<ArticuloDTO> get(@PathVariable Long id) {
		return ResponseEntity.ok(articuloService.get(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ArticuloDTO> actualizarArticulo(@PathVariable @NotNull Long id,
	                                                      @Valid @RequestBody ArticuloRequest articulo) {
		return ResponseEntity.ok(articuloService.actualizarArticulo(id, articulo));
	}
	
	@GetMapping
	@ResponseBody  // 👈 Esto asegura que devuelve JSON en lugar de intentar buscar una vista
	public ResponseEntity<List<ArticuloDTO>> listarArticulos(@RequestParam(required = false) String numero) {
		if (numero == null || numero.length() == 0) {
			return ResponseEntity.ok(articuloService.listarArticulos());
		}
		return ResponseEntity.ok(List.of(articuloService.getItem(numero)));
	}
	
	@GetMapping("/{id}/precio-vigente")
	public ResponseEntity<PrecioDTO> obtenerPrecioVigente(@PathVariable @NotNull Long id) {
		return ResponseEntity.ok(precioService.obtenerPrecioVigente(id));
	}
	
}

