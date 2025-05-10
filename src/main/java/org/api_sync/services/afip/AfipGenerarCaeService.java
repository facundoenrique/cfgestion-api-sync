package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.responses.CaeResponse;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AfipGenerarCaeService {
	private final AfipAuthentificationClient afipAuthentificationClient;
	private final EmpresaRepository empresaRepository;

	public CaeResponse generearCae(Long empresaId, Integer certificadoPuntoVenta, ComprobanteRequest comprobante) {
		
		Empresa empresa = empresaRepository.findById(empresaId)
				                  .orElseThrow(() -> new RuntimeException("No existe la empresa"));
		
		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(empresa.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(auth.getToken(), auth.getSign(), empresa.getCuit());
			
			CaeDTO caeDto = psoapClientSAAJ.getCae(comprobante);
			
			log.info("ARCA response: {}", caeDto);
			
			return CaeResponse.builder()
					       .cae(caeDto.getCae())
					       .caeFechaVto(caeDto.getCaeFechaVto())
					       .codeError(caeDto.getCodeError())
					       .messageError(caeDto.getMessageError())
					       .message(caeDto.getMessage())
					       .build();
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
}
