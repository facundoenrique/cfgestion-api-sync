package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.ClienteRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AfipCaeService {
	private final AfipAuthentificationClient afipAuthentificationClient;
	private final ClienteRepository clienteRepository;
	private final EmpresaRepository empresaRepository;
	
	public Integer consultarUltimoComprobante(Long clientId, Integer certificadoPuntoVenta, Integer puntoVenta) {
		
		Cliente cliente = clienteRepository.findById(clientId)
				                  .orElseThrow(() -> new RuntimeException("No existe el cliente"));

		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(cliente.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(auth.getToken(), auth.getSign(), cliente.getCuit());

			Integer ultimoComprobante = psoapClientSAAJ.llamarUltimaFE(puntoVenta);
			
			log.info("Ultimo comprabante {} en punto de venta :{}", ultimoComprobante, puntoVenta);
			
			return ultimoComprobante;
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	public Integer consultarUltimoComprobanteByEmpresa(Long empresaId, Integer certificadoPuntoVenta,
	                                                  Integer puntoVenta) {
		
		Empresa empresa = empresaRepository.findById(empresaId)
				                  .orElseThrow(() -> new RuntimeException("No existe el cliente"));
		
		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(empresa.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(auth.getToken(), auth.getSign(), empresa.getCuit());
			
			Integer ultimoComprobante = psoapClientSAAJ.llamarUltimaFE(puntoVenta);
			
			log.info("Ultimo comprabante {} en punto de venta :{}", ultimoComprobante, puntoVenta);
			
			return ultimoComprobante;
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return 0;
	}
	
}
