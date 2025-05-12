package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.services.afip.AfipConsultarCaeService;
import org.api_sync.services.afip.AfipGenerarCaeService;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/empresas/cae")
@RequiredArgsConstructor
public class CaeController {

	private final AfipConsultarCaeService afipConsultarCaeService;
	private final AfipGenerarCaeService afipGenerarCaeService;

	@GetMapping("/test-auth")
	public ResponseEntity<?> testAuth(Principal principal) {
		log.info("Test de autenticación en CaeController");
		Map<String, String> response = new HashMap<>();
		
		if (principal != null) {
			log.info("Usuario autenticado: {}", principal.getName());
			response.put("message", "¡Autenticación exitosa en CaeController!");
			response.put("username", principal.getName());
		} else {
			log.warn("Principal es null en la solicitud");
			response.put("message", "Error: Principal es null");
		}
		
		return ResponseEntity.ok(response);
	}

	@GetMapping("/ultimo")
	public Integer ultimo(
			@RequestParam("empresa") String empresa,
			@RequestParam("punto_venta") Integer puntoVenta,
			@RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta,
			@RequestParam("tipo_comprobante") Integer tipoComprobante,
			Principal principal) {
		
		log.info("Solicitud de último comprobante recibida para empresa: {} de usuario: {}", 
				empresa, principal != null ? principal.getName() : "desconocido");
		
		return afipConsultarCaeService.consultarUltimoComprobanteByEmpresa(
				empresa, certificadoPuntoVenta, puntoVenta, tipoComprobante);
	}

	@PostMapping
	public CaeResponse getCae(
			@RequestParam("empresa") Long empresa,
			@RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta,
			@RequestBody ComprobanteRequest comprobanteRequest,
			Principal principal) {
		
		log.info("Solicitud de CAE recibida para empresa: {} de usuario: {}", 
				empresa, principal != null ? principal.getName() : "desconocido");
		
		return afipGenerarCaeService.generarCae(empresa, certificadoPuntoVenta, comprobanteRequest);
	}
}
