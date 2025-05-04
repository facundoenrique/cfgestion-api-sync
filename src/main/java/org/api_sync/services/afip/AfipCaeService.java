package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AfipCaeService {
	private final AfipAuthentificationService afipAuthentificationService;
	
	public Integer consultarUltimoComprobante(Cliente cliente, Integer puntoVenta) {
		
		try {
			Authentication auth = afipAuthentificationService.getAuthentication(cliente.getId(), puntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(auth.getToken(), auth.getSign(), cliente.getCuit());

			Integer ultimoComprobante = psoapClientSAAJ.llamarUltimaFE(puntoVenta);
			
			log.info("Ultimo comprabante {} en punto de venta :{}", ultimoComprobante, puntoVenta);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}
}
