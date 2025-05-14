package org.api_sync.adapter.inbound.gestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.services.gestion.certificados.GestionCertificadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("gestion/certificados")
@RequiredArgsConstructor
public class GestionCertificadosController {

	private final GestionCertificadosService certificadoService;

	@PostMapping
	public ResponseEntity<String> subirCertificado(
			@RequestParam("file") MultipartFile file,
			@RequestParam("punto_venta") @NotNull(message = "punto_venta obligatorio") Integer puntoVenta,
			@RequestParam("empresa") @NotBlank(message = "No debe estar vacio") String uuid,
			@RequestParam("password") String password) {
		try {
			Certificado certificado = certificadoService.guardarCertificado(file, puntoVenta, uuid, password);
			return ResponseEntity.ok("Certificado guardado con ID: " + certificado.getId());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error al guardar el certificado: " + e.getMessage());
		}
	}
}
