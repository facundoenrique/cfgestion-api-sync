package org.api_sync.adapter.inbound.gestion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.services.afip.AfipConsultarCaeService;
import org.api_sync.services.afip.AfipGenerarCaeService;
import org.api_sync.services.afip.ComprobanteAfip;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


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

	//TODO: a futuro recibir un DatosComprobante completo y mappearlo a esto.
	//TODO: Podriamos hacernos cargo de reprocesar el comprobante si lo almacenamos en la base de datos.
	//TODO: Es costoso hacerlo y mantenerlo? si guardamos DatosComprobante matamos 2 pagaros de un tiro.
	//TODO: Seria muy bueno poder enviar el error de importes bien identificado y que desde la pc nos envien un mail
	// con los logs asi lo podemos revisar en el momento
	@PostMapping
	public CaeResponse getCae(
			@RequestParam("empresa") String empresaUuid,
			@RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta,
			@RequestBody ComprobanteRequest comprobanteRequest,
			Principal principal) {
		
		try {
			log.info("Solicitud de CAE recibida para empresa: {} de usuario: {}. request: {}",
					empresaUuid, principal != null ? principal.getName() : "desconocido", new ObjectMapper().writeValueAsString(comprobanteRequest));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		
		return afipGenerarCaeService.generarCae(empresaUuid, certificadoPuntoVenta, comprobanteRequest);
	}

	@GetMapping("/comprobantes-enviados")
	public List<ComprobanteAfip> ultimo(
			@RequestParam("empresa") String empresaUuid,
			@RequestParam("punto_venta") Integer puntoVenta,
			@RequestParam("certificado_punto_venta") Integer certificadoPuntoVenta, //Este indica que certificado usar
			@RequestParam("tipo_comprobante") Integer tipoComprobante,
			@RequestParam("numero_inicio") @Min(1) @Max(99999999) Integer numeroInicio,
			@RequestParam("numero_fin") @Min(1) @Max(99999999) Integer numeroFin,
			Principal principal) {
		
		if (numeroFin<numeroInicio) {
			throw new RuntimeException("Error en numeros a buscar");
		}
		
		if (numeroFin-numeroInicio > 20) {
			throw new RuntimeException("El rango debe ser menor a 20 comprobantes");
		}
		
		log.info("Solicitud de comprobantes recibida para empresa: {} de usuario: {}",
				empresaUuid, principal != null ? principal.getName() : "desconocido");
		
		return afipConsultarCaeService.consultarComprobantes(
				empresaUuid, certificadoPuntoVenta, puntoVenta, tipoComprobante, numeroInicio, numeroFin);
	}
}
