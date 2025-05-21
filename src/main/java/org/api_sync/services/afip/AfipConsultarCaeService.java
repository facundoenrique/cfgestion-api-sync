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
import java.util.ArrayList;
import java.util.List;

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
				auth,
				afipServiceConfig //TODO, mejorar, por como esta implementado se setea el endpoint para generar cae
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
					auth,
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

	public List<ComprobanteAfip> consultarComprobantes(String empresaUuid,
	                                                   Integer certificadoPuntoVenta,
	                                                   Integer puntoVenta,
	                                                   Integer tipoComprobante,
	                                                   Integer numeroInicio,
	                                                   Integer numeroFin) {
		
		Empresa empresa = empresaRepository.findByUuid(empresaUuid)
				                  .orElseThrow(() -> new RuntimeException("No existe la empresa"));
		
		try {
			Authentication auth = afipAuthentificationClient.getAuthentication(empresa.getCuit(), certificadoPuntoVenta);
			
			PSOAPClientSAAJ psoapClientSAAJ = new PSOAPClientSAAJ(
					auth,
					afipServiceConfig
			);
			List<ComprobanteAfip> comprobantes = new ArrayList<>();
			
			//TODO: Ver como pedir todos juntos, con una sola request.
			for (int i=numeroInicio; i<=numeroFin; i++) {
				comprobantes.add(psoapClientSAAJ.getComprobante(puntoVenta, tipoComprobante, i));
			}
			
			log.info("Cantidad de comprobantes recuperados {} en punto de venta: {}", comprobantes.size(), puntoVenta);
			
			return comprobantes;
			
		} catch (Exception e) {
			log.error("Error al consultar comprobantes: {}", e.getMessage(), e);
			throw new RuntimeException("Error al consultar último comprobante", e);
		}
	}
}
