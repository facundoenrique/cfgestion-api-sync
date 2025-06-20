package org.api_sync.adapter.inbound.red;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.gestion.Usuario;
import org.api_sync.services.red.RedUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/red/usuarios")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RedUsuarioController {
	private final RedUsuarioService redUsuarioService;
	
	@PostMapping
	public ResponseEntity<Usuario> crearUsuario(
			@RequestParam @NotBlank(message = "La empresa es obligatoria") String empresa,
			@RequestParam @NotBlank(message = "El username es obligatorio") String username,
			@RequestParam @NotBlank(message = "La contraseña es obligatoria") String password,
			@RequestParam @NotNull(message = "El cliente es obligatorio") Long clienteId) {
		
		log.info("Creando nuevo usuario: {} para empresa: {}", username, empresa);
		Usuario usuario = redUsuarioService.createUser(username, password, empresa, clienteId);
		return ResponseEntity.ok(usuario);
	}
}
