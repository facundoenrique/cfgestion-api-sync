package org.api_sync.services.usuarios;


import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.adapter.outbound.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;

	public Optional<Usuario> login(String username, String password) {
		Optional<Usuario> user = usuarioRepository.findByNombre(username);
		
		if (user.isPresent() && password.equals(user.get().getPassword())) {
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
}
