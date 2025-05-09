package org.api_sync.services.gestion.certificados;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.api_sync.adapter.outbound.entities.Certificado;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.CertificadosRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.domain.Origen;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class GestionCertificadosService {

	private final CertificadosRepository certificadosRepository;
	private final EmpresaRepository empresaRepository;
	
	
	public Certificado guardarCertificado(MultipartFile file, Integer puntoVenta, Long clienteId, String password) throws IOException {
		Empresa empresa = empresaRepository.findById(clienteId)
				                  .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con ID: " + clienteId));
		
		String pass = StringUtils.isNotBlank(password) ? password : "mastermix";
		
		Certificado certificado = new Certificado();
		certificado.setArchivo(file.getBytes());
		certificado.setPuntoVenta(puntoVenta);
		certificado.setCuit(empresa.getCuit());
		certificado.setPassword(pass);
		certificado.setFechaCreado(Date.from(Instant.now()));
		certificado.setOrigen(Origen.GESTION);
		
		return certificadosRepository.save(certificado);
	}
}
