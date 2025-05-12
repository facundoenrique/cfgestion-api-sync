package org.api_sync.services.usuarios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

		Optional<Usuario> user = usuarioRepository.findByNombre(username);
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
		Optional<Usuario> user = usuarioRepository.findByNombre(username);
		
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
	public Usuario crearUsuario(String nombre, String password, String empresaUuid) {
		log.info("Creando usuario: {}, empresa: {}", nombre, empresaUuid);
		if (usuarioRepository.findByNombre(nombre).isPresent()) {
			log.warn("Intento de crear usuario ya existente: {}", nombre);
			throw new RuntimeException("El usuario ya existe");
		}

		Empresa empresa = empresaRepository.findByUuid(empresaUuid)
			.orElseThrow(() -> {
				log.error("Empresa no encontrada con UUID: {}", empresaUuid);
				return new RuntimeException("Empresa no encontrada");
			});

		Usuario usuario = new Usuario();
		usuario.setNombre(nombre);
		usuario.setPassword(passwordEncoder.encode(password));
		usuario.setEmpresa(empresa);
		
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

		if (!usuario.getNombre().equals(nombre) && 
			usuarioRepository.findByNombre(nombre).isPresent()) {
			log.warn("Intento de actualizar a un nombre de usuario ya en uso: {}", nombre);
			throw new RuntimeException("El nombre de usuario ya está en uso");
		}

		Empresa empresa = empresaRepository.findByUuid(empresaUuid)
			.orElseThrow(() -> {
				log.error("Empresa no encontrada con UUID: {}", empresaUuid);
				return new RuntimeException("Empresa no encontrada");
			});

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
