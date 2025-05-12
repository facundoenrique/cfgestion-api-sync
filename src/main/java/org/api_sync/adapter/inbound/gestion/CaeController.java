package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.services.afip.AfipConsultarCaeService;
import org.api_sync.services.afip.AfipGenerarCaeService;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@RestController
@RequestMapping("/empresas/cae")
@RequiredArgsConstructor
public class CaeController {

	private final AfipConsultarCaeService afipConsultarCaeService;
	private final AfipGenerarCaeService afipGenerarCaeService;

	@GetMapping("/ultimo")
	public Integer ultimo(
			@RequestParam("empresa") String empresaUuid,
			@RequestParam("punto_venta") Integer puntoVenta,
			@RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta, //Este indica que certificado usar
			@RequestParam("tipo_comprobante") Integer tipoComprobante,
			Principal principal) {
		
		log.info("Solicitud de último comprobante recibida para empresa: {} de usuario: {}",
				empresaUuid, principal != null ? principal.getName() : "desconocido");
		
		return afipConsultarCaeService.consultarUltimoComprobanteByEmpresa(
				empresaUuid, certificadoPuntoVenta, puntoVenta, tipoComprobante);
	}

	@PostMapping
	public CaeResponse getCae(
			@RequestParam("empresa") String empresaUuid,
			@RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta,
			@RequestBody ComprobanteRequest comprobanteRequest,
			Principal principal) {
		
		log.info("Solicitud de CAE recibida para empresa: {} de usuario: {}",
				empresaUuid, principal != null ? principal.getName() : "desconocido");
		
		return afipGenerarCaeService.generarCae(empresaUuid, certificadoPuntoVenta, comprobanteRequest);
	}
}
