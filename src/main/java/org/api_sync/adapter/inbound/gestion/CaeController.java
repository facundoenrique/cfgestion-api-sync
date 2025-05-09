package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import org.api_sync.services.afip.AfipCaeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empresas/cae")
@RequiredArgsConstructor
public class CaeController {

	private final AfipCaeService afipCaeService;

	@GetMapping("/ultimo")
	public Integer ultimo(@RequestParam("empresa") Long empresa,
	                      @RequestParam("punto_venta") Integer puntoVenta,
	                      @RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta) {
		
		return afipCaeService.consultarUltimoComprobanteByEmpresa(empresa, certificadoPuntoVenta, puntoVenta);
	}
}
