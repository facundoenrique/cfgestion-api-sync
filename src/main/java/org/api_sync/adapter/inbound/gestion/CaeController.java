package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.services.afip.AfipConsultarCaeService;
import org.api_sync.services.afip.AfipGenerarCaeService;
import org.api_sync.services.afip.ComprobanteRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas/cae")
@RequiredArgsConstructor
public class CaeController {

	private final AfipConsultarCaeService afipConsultarCaeService;
	private final AfipGenerarCaeService afipGenerarCaeService;

	@GetMapping("/ultimo")
	public Integer ultimo(@RequestParam("empresa") Long empresa,
	                      @RequestParam("punto_venta") Integer puntoVenta,
	                      @RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta,
						  @RequestParam("tipo_comprobante") Integer tipoComprobante) {
		
		return afipConsultarCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta, tipoComprobante);
	}

	@PostMapping
	public CaeResponse getCae(@RequestParam("empresa") Long empresa,
	                          @RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta,
	                          @RequestBody ComprobanteRequest comprobanteRequest) {
		
		return afipGenerarCaeService.generearCae(empresa, certificadoPuntoVenta, comprobanteRequest);
	}
}
