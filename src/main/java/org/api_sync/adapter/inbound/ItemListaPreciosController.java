package org.api_sync.adapter.inbound;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.PrecioRequest;
import org.api_sync.services.lista_precios.ItemListaPreciosService;
import org.api_sync.services.lista_precios.dto.ItemListaPreciosDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item-lista-precios")
@RequiredArgsConstructor
public class ItemListaPreciosController {

	private final ItemListaPreciosService itemListaPreciosService;

	@PutMapping("/{id}/precio")
	public ResponseEntity<ItemListaPreciosDTO> actualizarPrecio(
			@PathVariable Long id,
			@RequestBody @Valid PrecioRequest precioRequest) {
		
		ItemListaPreciosDTO actualizado = itemListaPreciosService.actualizarPrecio(id, precioRequest);
		return ResponseEntity.ok(actualizado);
	}
}
