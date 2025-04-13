package org.api_sync.adapter.inbound;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.request.ArticuloRequest;
import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.adapter.inbound.request.ListaPreciosUpdateRequest;
import org.api_sync.services.articulos.dto.ArticuloDTO;
import org.api_sync.services.lista_precios.ListaPreciosService;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/listas-de-precios")
@RequiredArgsConstructor
public class ListaPreciosController {

	private final ListaPreciosService listaPreciosService;
	
	@PostMapping
	public ResponseEntity<ListaPreciosDTO> crearListaDePrecios(@RequestBody ListaPreciosRequest request) {
		return ResponseEntity.ok(listaPreciosService.crearListaDePrecios(request));
	}


	@PatchMapping("/{id}/items")
	public ResponseEntity<ArticuloDTO> addItem(@PathVariable Long id,
	                                           @Valid @RequestBody ArticuloRequest articulo) {
		ArticuloDTO dto = listaPreciosService.addItem(articulo, id);
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping
	public ResponseEntity<Page<ListaPreciosDTO>> listarListasDePrecios(
			@RequestParam(required = false) LocalDate fechaDesde,
			@RequestParam(required = false) LocalDate fechaHasta,
			@RequestParam(required = false) Long proveedorId,
			@RequestParam(required = false) String nombre,
			@PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable) {
		
		return ResponseEntity.ok(listaPreciosService.listarListasDePrecios(fechaDesde, fechaHasta, proveedorId,
				nombre, pageable));
	}

	@GetMapping("{id}")
	public ResponseEntity<ListaPreciosDTO> getListaDePrecios(@PathVariable Long id) {
		Optional<ListaPreciosDTO> lista = listaPreciosService.getListaPrecio(id);
		if (lista.isPresent()) {
			return ResponseEntity.ok(lista.get());
		}
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/cargar")
	public ResponseEntity<Response> cargarListaPrecios(@RequestParam("file") MultipartFile file,
	                                                 @RequestParam("proveedor") Long proveedorId,
	                                                 @RequestParam("nombre_lista") String nombre) {
		listaPreciosService.procesarArchivo(file, proveedorId, nombre);
		Response response = new Response("Archivo procesado correctamente", "ok");
		log.info("Archivo procesado correctamente");
		return ResponseEntity.ok(response);
	}

	@PutMapping("{id}")
	public ResponseEntity<ListaPreciosDTO> actualizarListaPrecios(@PathVariable Long id,
	                                                              @RequestBody ListaPreciosUpdateRequest updateRequest) {
		
		ListaPreciosDTO updatedListaPrecios = listaPreciosService.actualizarListaPrecios(id, updateRequest);
		return ResponseEntity.ok(updatedListaPrecios);
	}
	
}

