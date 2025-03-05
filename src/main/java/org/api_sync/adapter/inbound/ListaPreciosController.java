package org.api_sync.adapter.inbound;


import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.request.ListaPreciosRequest;
import org.api_sync.services.lista_precios.ListaPreciosService;
import org.api_sync.services.lista_precios.dto.ListaPreciosDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

	@PostMapping("/cargar")
	public ResponseEntity<String> cargarListaPrecios(@RequestParam("file") MultipartFile file,
	                                                 @RequestParam("proveedor") Long proveedorId,
	                                                 @RequestParam("nombre_lista") String nombre) {
		listaPreciosService.procesarArchivo(file, proveedorId, nombre);
		return ResponseEntity.ok("Archivo procesado correctamente");
	}
}

