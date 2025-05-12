package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.afip.config.AfipServiceConfig;
import org.api_sync.services.afip.model.CaeDTO;
import org.api_sync.services.afip.model.ComprobanteRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AfipGenerarCaeService {
	private final AfipAuthentificationClient afipAuthentificationClient;
	private final EmpresaRepository empresaRepository;
	private final AfipServiceConfig afipServiceConfig;

	public CaeResponse generarCae(String empresaId, Integer certificadoPuntoVenta, ComprobanteRequest comprobante) {
		Empresa empresa = empresaRepository.findByUuid(empresaId)
				                  .orElseThrow(() -> new RuntimeException("No existe la empresa"));
		
		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(empresa.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(
				auth.getToken(), 
				auth.getSign(), 
				empresa.getCuit(),
				afipServiceConfig
			);
			
			CaeDTO caeDto = psoapClientSAAJ.getCae(comprobante);
			
			log.info("ARCA response: {}", caeDto);
			
			return CaeResponse.builder()
					       .cae(caeDto.getCae())
					       .caeFechaVto(caeDto.getCaeFchVto())
					       .messageError(null)
					       .codeError(null)
					       .message(null)
					       .build();
			
		} catch (Exception e) {
			log.error("Error al generar CAE: {}", e.getMessage(), e);
			throw new RuntimeException("Error al generar CAE", e);
		}
	}
	
}
