package org.api_sync.adapter.inbound.red;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.PrecioRequest;
import org.api_sync.services.lista_precios.ItemListaPreciosService;
import org.api_sync.services.lista_precios.dto.ItemListaPreciosDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/red/item-lista-precios")
@RequiredArgsConstructor
public class ItemListaPreciosController {

	private final ItemListaPreciosService itemListaPreciosService;

	@GetMapping
	public ResponseEntity<ItemListaPreciosDTO> get(Long id) {
		return ResponseEntity.ok(itemListaPreciosService.get(id));
	}
	
	@PutMapping("/{id}/precio")
	public ResponseEntity<ItemListaPreciosDTO> actualizarPrecio(
			@PathVariable Long id,
			@RequestBody @Valid PrecioRequest precioRequest) {
		
		ItemListaPreciosDTO actualizado = itemListaPreciosService.actualizarPrecio(id, precioRequest);
		return ResponseEntity.ok(actualizado);
	}
	
}
