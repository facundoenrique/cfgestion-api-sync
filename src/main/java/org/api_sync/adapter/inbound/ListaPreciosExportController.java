package org.api_sync.adapter.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.services.lista_precios.ExportPriceListService;
import org.api_sync.services.lista_precios.dto.CvsDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/listas-de-precios")
@RequiredArgsConstructor
public class ListaPreciosExportController {

	private final ExportPriceListService exportPriceListService;

	@GetMapping("/csv/{id}")
	public ResponseEntity<byte[]> exportCsv(@PathVariable Long id) {
		Optional<CvsDTO> cvsDTO = exportPriceListService.generateCsv(id);
		
		if (cvsDTO.isEmpty()) {
			throw new RuntimeException("error");
		}
		
		String nombre = cvsDTO.get().getNombre();
		String data = cvsDTO.get().getData();
		
		return ResponseEntity.ok()
				       .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+nombre+".cvs")
				       .contentType(MediaType.parseMediaType("text/csv"))
				       .body(data.getBytes());
	}
}
