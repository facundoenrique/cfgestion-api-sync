package org.api_sync.services.red;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.Cliente;
import org.api_sync.adapter.outbound.entities.UsuarioRelacion;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.entities.gestion.Usuario;
import org.api_sync.adapter.outbound.repository.ClienteRepository;
import org.api_sync.adapter.outbound.repository.UsuarioRelacionRepository;
import org.api_sync.adapter.outbound.repository.UsuarioRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.exceptions.EmpresaNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRelacionRepository usuarioRelacionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario createUser(String username, String password, String empresaUuid, Long clienteId) {
        log.info("Creando nuevo usuario: {}", username);
        
        Empresa empresa = empresaRepository.findByUuid(empresaUuid)
            .orElseThrow(() -> new EmpresaNotFoundException(empresaUuid));

        if (usuarioRepository.findByNombre(username).isPresent()) {
            log.error("Usuario ya existe: {}", username);
            throw new RuntimeException("El usuario ya existe");
        }

        // Validar que la empresa existe
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> {
                log.error("Empresa no encontrada: {}", empresa);
                return new RuntimeException("Empresa no encontrada");
            });

        // Obtener el siguiente código disponible para la empresa
        Integer maxCodigo = usuarioRepository.findMaxCodigoByEmpresa(empresa.getId());
        Integer nextCodigo = (maxCodigo == null) ? 1 : maxCodigo + 1;

        // Crear usuario
        Usuario usuario = Usuario.builder()
            .nombre(username)
            .password(passwordEncoder.encode(password))
            .empresa(empresa)
            .codigo(nextCodigo)
            .build();
        
        usuario = usuarioRepository.save(usuario);

        // Crear relación con el cliente
        UsuarioRelacion relacion = UsuarioRelacion.builder()
            .usuarioId(usuario.getId())
            .tipoRelacion(UsuarioRelacion.TipoRelacion.CLIENTE)
            .entidadId(cliente.getId())
            .build();
        
        usuarioRelacionRepository.save(relacion);

        return usuario;
    }
} 