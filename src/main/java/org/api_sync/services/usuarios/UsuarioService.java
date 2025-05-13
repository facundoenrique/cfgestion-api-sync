package org.api_sync.services.usuarios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.inbound.gestion.request.UsuarioRequest;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.UsuarioRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	private final EmpresaRepository empresaRepository;
	private final PasswordEncoder passwordEncoder;

	public Optional<Usuario> login(String username, String password, String empresaUuid, Integer sucursalId) {
		log.debug("Iniciando proceso de login para usuario: {}, empresa: {}", username, empresaUuid);
		
		Optional<Empresa> empresa = empresaRepository.findByUuid(empresaUuid);
		if (empresa.isEmpty()) {
			log.warn("Empresa no encontrada con UUID: {}", empresaUuid);
			return Optional.empty();
		}

		Optional<Usuario> user = usuarioRepository.findByNombreAndEmpresaAndEliminado(username, empresa.get(), 0);
		if (user.isEmpty()) {
			log.warn("Usuario no encontrado: {}", username);
			return Optional.empty();
		}
		
		try {
			boolean passwordMatches = passwordEncoder.matches(password, user.get().getPassword());
			log.debug("Resultado de verificación de contraseña para usuario {}: {}", username, passwordMatches);
			
			if (passwordMatches && user.get().getEmpresa().getId().equals(empresa.get().getId())) {
				log.info("Login exitoso para usuario: {}", username);
				return user;
			}
			
			if (!passwordMatches) {
				log.warn("Contraseña incorrecta para usuario: {}", username);
			} else {
				log.warn("Usuario no pertenece a la empresa indicada");
			}
			
			return Optional.empty();
		} catch (Exception e) {
			log.error("Error al verificar contraseña para usuario: {}", username, e);
			return Optional.empty();
		}
	}

	public Usuario findBy(String username) {
		log.debug("Buscando usuario por nombre: {}", username);
		Optional<Usuario> user = usuarioRepository.findByNombreAndEliminado(username, 0);
		
		if (user.isPresent()) {
			return user.get();
		}
		log.error("Usuario no encontrado al refrescar token: {}", username);
		throw new RuntimeException("Error al refrescar el token");
	}

	public Usuario create(Usuario usuario) {
		log.info("Creando nuevo usuario: {}", usuario.getNombre());
		// Encriptar la contraseña antes de guardar
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		return usuarioRepository.save(usuario);
	}

	public Usuario update(Usuario usuario) {
		log.info("Actualizando usuario: {}", usuario.getNombre());
		// Si la contraseña ha cambiado, encriptarla
		Optional<Usuario> existingUser = usuarioRepository.findById(usuario.getId());
		if (existingUser.isPresent() && !existingUser.get().getPassword().equals(usuario.getPassword())) {
			log.debug("Contraseña modificada, encriptando...");
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		return usuarioRepository.save(usuario);
	}

	@Transactional
	public Usuario crearUsuario(UsuarioRequest usuarioRequest) {
		Empresa empresa = empresaRepository.findByUuid(usuarioRequest.getEmpresa())
				                  .orElseThrow(() -> {
					                  log.error("Empresa no encontrada con UUID: {}", usuarioRequest.getEmpresa());
					                  return new RuntimeException("Empresa no encontrada");
				                  });
		
		log.info("Creando usuario: {}, empresa: {}", usuarioRequest.getNombre(), usuarioRequest.getEmpresa());
		if (usuarioRepository.findByNombreAndEmpresaAndEliminado(usuarioRequest.getNombre(), empresa, 0).isPresent()) {
			log.warn("Intento de crear usuario ya existente: {}", usuarioRequest.getNombre());
			throw new RuntimeException("El usuario ya existe");
		}

		Usuario usuario = new Usuario();
		usuario.setNombre(usuarioRequest.getNombre());
		usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
		usuario.setEmpresa(empresa);
		usuario.setCodigo(usuarioRequest.getCodigo());
		
		return usuarioRepository.save(usuario);
	}

	@Transactional
	public Usuario actualizarUsuario(Long id, UsuarioRequest usuarioRequest) {
		log.info("Actualizando usuario ID: {}, nuevo nombre: {}", id, usuarioRequest.getNombre());
		Usuario usuario = usuarioRepository.findById(id)
			.orElseThrow(() -> {
				log.error("Usuario no encontrado con ID: {}", id);
				return new RuntimeException("Usuario no encontrado");
			});
		
		Empresa empresa = empresaRepository.findByUuid(usuarioRequest.getEmpresa())
				                  .orElseThrow(() -> {
					                  log.error("Empresa no encontrada con UUID: {}", usuarioRequest.getEmpresa());
					                  return new RuntimeException("Empresa no encontrada");
				                  });
		
		if (!usuario.getNombre().equals(usuarioRequest.getNombre()) && 
			usuarioRepository.findByNombreAndEmpresa(usuarioRequest.getNombre(), empresa).isPresent()) {
			log.warn("Intento de actualizar a un nombre de usuario ya en uso: {}", usuarioRequest.getNombre());
			throw new RuntimeException("El nombre de usuario ya está en uso");
		}

		usuario.setNombre(usuarioRequest.getNombre());
		if (usuarioRequest.getPassword() != null && !usuarioRequest.getPassword().isEmpty()) {
			log.debug("Actualizando contraseña del usuario");
			usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
		}
		usuario.setEmpresa(empresa);
		if (usuarioRequest.getCodigo() != null) {
			usuario.setCodigo(usuarioRequest.getCodigo());
		}
		
		return usuarioRepository.save(usuario);
	}

	@Transactional
	public Usuario actualizarUsuario(Long id, String nombre, String password, String empresaUuid) {
		log.info("Actualizando usuario ID: {}, nuevo nombre: {}", id, nombre);
		Usuario usuario = usuarioRepository.findById(id)
			.orElseThrow(() -> {
				log.error("Usuario no encontrado con ID: {}", id);
				return new RuntimeException("Usuario no encontrado");
			});
		
		Empresa empresa = empresaRepository.findByUuid(empresaUuid)
				                  .orElseThrow(() -> {
					                  log.error("Empresa no encontrada con UUID: {}", empresaUuid);
					                  return new RuntimeException("Empresa no encontrada");
				                  });
		
		if (!usuario.getNombre().equals(nombre) && 
			usuarioRepository.findByNombreAndEmpresa(nombre, empresa).isPresent()) {
			log.warn("Intento de actualizar a un nombre de usuario ya en uso: {}", nombre);
			throw new RuntimeException("El nombre de usuario ya está en uso");
		}

		usuario.setNombre(nombre);
		if (password != null && !password.isEmpty()) {
			log.debug("Actualizando contraseña del usuario");
			usuario.setPassword(passwordEncoder.encode(password));
		}
		usuario.setEmpresa(empresa);
		
		return usuarioRepository.save(usuario);
	}

	public List<Usuario> obtenerTodosLosUsuarios() {
		log.debug("Obteniendo todos los usuarios");
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> obtenerUsuarioPorId(Long id) {
		log.debug("Buscando usuario por ID: {}", id);
		return usuarioRepository.findById(id);
	}

	public Optional<Usuario> obtenerUsuarioPorNombre(String nombre) {
		log.debug("Buscando usuario por nombre: {}", nombre);
		return usuarioRepository.findByNombre(nombre);
	}

	public Optional<Usuario> obtenerUsuarioPorNombre(String nombre, String empresaUuid) {
		log.debug("Buscando usuario por nombre: {} y empresa UUID: {}", nombre, empresaUuid);
		Optional<Empresa> empresa = empresaRepository.findByUuid(empresaUuid);
		if (empresa.isEmpty()) {
			log.warn("Empresa no encontrada con UUID: {}", empresaUuid);
			return Optional.empty();
		}
		return usuarioRepository.findByNombreAndEmpresaAndEliminado(nombre, empresa.get(), 0);
	}

	@Transactional
	public void eliminarUsuario(Long id) {
		log.info("Eliminando usuario con ID: {}", id);
		if (!usuarioRepository.existsById(id)) {
			log.error("Intento de eliminar usuario inexistente con ID: {}", id);
			throw new RuntimeException("Usuario no encontrado");
		}
		usuarioRepository.deleteById(id);
	}
}
