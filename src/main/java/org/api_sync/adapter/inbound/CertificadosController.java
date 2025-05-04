package org.api_sync.adapter.inbound;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.services.certificados.CertificadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/certificados")
@RequiredArgsConstructor
public class CertificadosController {

	private final CertificadosService certificadoService;

	@PostMapping
	public ResponseEntity<String> subirCertificado(
			@RequestParam("file") MultipartFile file,
			@RequestParam("punto_venta") Integer puntoVenta,
			@RequestParam("empresa_id") Long empresaId,
			@RequestParam("password") String password) {
		try {
			Certificado certificado = certificadoService.guardarCertificado(file, puntoVenta, empresaId, password);
			return ResponseEntity.ok("Certificado guardado con ID: " + certificado.getId());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error al guardar el certificado: " + e.getMessage());
		}
	}
}
