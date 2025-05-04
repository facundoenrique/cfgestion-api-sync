package org.api_sync.services.certificados;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.repository.CertificadosRepository;
import org.api_sync.adapter.outbound.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CertificadosService {

	private final CertificadosRepository certificadosRepository;
	private final ClienteRepository clienteRepository;
	
	
	public Certificado guardarCertificado(MultipartFile file, Integer puntoVenta, Long clienteId) throws IOException {
		Cliente cliente = clienteRepository.findById(clienteId)
				                  .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrada con ID: " + clienteId));
		
		Certificado certificado = new Certificado();
		certificado.setArchivo(file.getBytes());
		certificado.setPuntoVenta(puntoVenta);
		certificado.setCliente(cliente);
		certificado.setPassword("mastermix");
		certificado.setFechaCreado(Date.from(Instant.now()));
		
		return certificadosRepository.save(certificado);
	}
}
