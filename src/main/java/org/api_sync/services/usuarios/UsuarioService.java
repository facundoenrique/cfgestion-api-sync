package org.api_sync.services.usuarios;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.UsuarioRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	private final EmpresaRepository empresaRepository;
	private final PasswordEncoder passwordEncoder;

	public Optional<Usuario> login(String username, String password, String empresaUuid) {
		Optional<Empresa> empresa = empresaRepository.findByUuid(empresaUuid);
		if (empresa.isEmpty()) {
			return Optional.empty();
		}

		Optional<Usuario> user = usuarioRepository.findByNombre(username);
		
		if (user.isPresent() && 
			passwordEncoder.matches(password, user.get().getPassword()) && 
			user.get().getEmpresa().equals(empresa.get().getId())) {
			return user;
		}
		
		return Optional.empty();
	}

	public Usuario findBy(String username) {
		Optional<Usuario> user = usuarioRepository.findByNombre(username);
		
		if (user.isPresent()) {
			return user.get();
		}
		throw new RuntimeException("Error al refrescar el token");
	}

	public Usuario create(Usuario usuario) {
		// Encriptar la contraseña antes de guardar
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		return usuarioRepository.save(usuario);
	}

	public Usuario update(Usuario usuario) {
		// Si la contraseña ha cambiado, encriptarla
		Optional<Usuario> existingUser = usuarioRepository.findById(usuario.getId());
		if (existingUser.isPresent() && !existingUser.get().getPassword().equals(usuario.getPassword())) {
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		return usuarioRepository.save(usuario);
	}
}
