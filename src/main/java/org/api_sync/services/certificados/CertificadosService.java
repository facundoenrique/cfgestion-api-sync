package org.api_sync.services.certificados;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
	
	
	public Certificado guardarCertificado(MultipartFile file, Integer puntoVenta, Long clienteId, String password) throws IOException {
		Cliente cliente = clienteRepository.findById(clienteId)
				                  .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrada con ID: " + clienteId));
		
		String pass = StringUtils.isNotBlank(password) ? password : "mastermix";
		
		Certificado certificado = new Certificado();
		certificado.setArchivo(file.getBytes());
		certificado.setPuntoVenta(puntoVenta);
		certificado.setCuit(cliente.getCuit());
		certificado.setPassword(pass);
		certificado.setFechaCreado(Date.from(Instant.now()));
		
		return certificadosRepository.save(certificado);
	}
}
