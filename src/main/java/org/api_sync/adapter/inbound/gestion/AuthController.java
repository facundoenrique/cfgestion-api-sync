package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.gestion.utils.JwtUtil;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.services.usuarios.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;
	private final Set<String> refreshTokens = new HashSet<>();
	private final UsuarioService usuarioService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("pc_name") String pcName,
			@RequestParam("punto_venta") Integer puntoVenta,
			@RequestParam("empresa_uuid") String empresaUuid) {
		
		// Validar que los campos no estén vacíos
		if (username == null || username.trim().isEmpty() ||
			password == null || password.trim().isEmpty() ||
			empresaUuid == null || empresaUuid.trim().isEmpty() ||
			puntoVenta == null) {
			return ResponseEntity.badRequest().body("Todos los campos son requeridos");
		}

		Optional<Usuario> user = usuarioService.login(username, password, empresaUuid);
		
		if (user.isPresent()) {
			String accessToken = jwtUtil.generateAccessToken(user.get(), pcName, puntoVenta);
			String refreshToken = jwtUtil.generateRefreshToken(username);
			refreshTokens.add(refreshToken);
			
			Map<String, String> tokens = new HashMap<>();
			tokens.put("accessToken", accessToken);
			tokens.put("refreshToken", refreshToken);
			
			return ResponseEntity.ok(tokens);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@RequestParam("refresh_token") String refreshToken) {
		try {
			if (!refreshTokens.contains(refreshToken)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de refresco inválido");
			}

			String username = jwtUtil.getUsername(refreshToken);
			Usuario user = usuarioService.findBy(username);
			
			String newAccessToken = jwtUtil.generateAccessToken(user, "unknown", 0);
			
			Map<String, String> tokens = new HashMap<>();
			tokens.put("accessToken", newAccessToken);
			
			return ResponseEntity.ok(tokens);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al refrescar el token");
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestParam String refreshToken) {
		refreshTokens.remove(refreshToken);
		return ResponseEntity.ok("Logged out successfully");
	}
}
