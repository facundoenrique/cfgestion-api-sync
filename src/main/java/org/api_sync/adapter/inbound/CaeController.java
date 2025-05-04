package org.api_sync.adapter.inbound;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.ClienteResponse;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.services.afip.AfipCaeService;
import org.api_sync.services.clientes.ClienteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cae")
@RequiredArgsConstructor
public class CaeController {

	private final AfipCaeService afipCaeService;
	private final ClienteService clienteService;

	@GetMapping("/ultimo")
	public Integer ultimo(@RequestParam("client_id") Long clientId,
	                      @RequestParam("punto_venta") Integer puntoVenta) {
		
		ClienteResponse clienteResponse = clienteService.getCustomerById(clientId);
		Cliente cliente = Cliente.builder()
				                  .id(clienteResponse.getId())
				                  .cuit(clienteResponse.getCuit())
				                  .build();
		
		return afipCaeService.consultarUltimoComprobante(cliente, puntoVenta);
	}
}
