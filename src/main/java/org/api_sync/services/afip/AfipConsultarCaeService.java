package org.api_sync.services.afip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Authentication;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.ClienteRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.afip.config.AfipServiceConfig;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AfipConsultarCaeService {
	private final AfipAuthentificationClient afipAuthentificationClient;
	private final ClienteRepository clienteRepository;
	private final EmpresaRepository empresaRepository;
	private final AfipServiceConfig afipServiceConfig;
	
	public Integer consultarUltimoComprobanteByCliente(Long clientId, Integer certificadoPuntoVenta,
	                                                  Integer puntoVenta,
													  Integer tipoComprobante) {
		
		Cliente cliente = clienteRepository.findById(clientId)
				                  .orElseThrow(() -> new RuntimeException("No existe el cliente"));

		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(cliente.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(
				auth.getToken(), 
				auth.getSign(), 
				cliente.getCuit(),
				afipServiceConfig
			);

			Integer ultimoComprobante = psoapClientSAAJ.searchUltimaFacturaElectronica(puntoVenta, tipoComprobante);
			
			log.info("Ultimo comprobante {} en punto de venta: {}", ultimoComprobante, puntoVenta);
			
			return ultimoComprobante;
			
		} catch (Exception e) {
			log.error("Error al consultar último comprobante: {}", e.getMessage(), e);
			throw new RuntimeException("Error al consultar último comprobante", e);
		}
	}

	public Integer consultarUltimoComprobanteByEmpresa(String empresaUuid, Integer certificadoPuntoVenta,
	                                                  Integer puntoVenta, Integer tipoComprobante) {
		
		Empresa empresa = empresaRepository.findByUuid(empresaUuid)
				                  .orElseThrow(() -> new RuntimeException("No existe la empresa"));
		
		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(empresa.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(
				auth.getToken(), 
				auth.getSign(), 
				empresa.getCuit(),
				afipServiceConfig
			);
			
			Integer ultimoComprobante = psoapClientSAAJ.searchUltimaFacturaElectronica(puntoVenta, tipoComprobante);
			
			log.info("Ultimo comprobante {} en punto de venta: {}", ultimoComprobante, puntoVenta);
			
			return ultimoComprobante;
			
		} catch (Exception e) {
			log.error("Error al consultar último comprobante: {}", e.getMessage(), e);
			throw new RuntimeException("Error al consultar último comprobante", e);
		}
	}
	
}
