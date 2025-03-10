package org.api_sync.adapter.inbound;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.services.lista_precios.ListaPreciosService;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
	
	@GetMapping
	public ResponseEntity<List<ListaPreciosDTO>> listarListasDePrecios() {
		return ResponseEntity.ok(listaPreciosService.listarListasDePrecios());
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
		log.info("Nombre lista: {}", nombre);
		System.out.println("Nombre lista: "+ nombre);
		listaPreciosService.procesarArchivo(file, proveedorId, nombre);
		Response response = new Response("Archivo procesado correctamente", "ok");
		log.info("Archivo procesado correctamente");
		return ResponseEntity.ok(response);
	}

	
}

