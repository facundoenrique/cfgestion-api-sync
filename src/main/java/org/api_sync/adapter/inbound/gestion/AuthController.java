package org.api_sync.adapter.inbound.gestion;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.gestion.utils.JwtUtil;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.services.usuarios.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Map<String, String>> login(@RequestParam String username,
	                                                 @RequestParam String password,
	                                                 @RequestParam String pcName,
	                                                 @RequestParam Integer puntoVentaFacturacion) {
		
		Optional<Usuario> user = usuarioService.login(username, password);
		
		if (user.isPresent()) {
			String accessToken = jwtUtil.generateAccessToken(user.get(), pcName, puntoVentaFacturacion);
			String refreshToken = jwtUtil.generateRefreshToken(username);
			refreshTokens.add(refreshToken);
			Map<String, String> tokens = Map.of(
					"accessToken", accessToken,
					"refreshToken", refreshToken
			);
			return ResponseEntity.ok(tokens);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refresh(@RequestParam String refreshToken) {
		if (!refreshTokens.contains(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		try {
			String username = jwtUtil.getUsername(refreshToken);
			Usuario user = usuarioService.findBy(username);
			String accessToken = jwtUtil.generateAccessToken(user, "unknown", 0);
			Map<String, String> tokens = Map.of(
					"accessToken", accessToken
			);
			return ResponseEntity.ok(tokens);
		} catch (JwtException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestParam String refreshToken) {
		refreshTokens.remove(refreshToken);
		return ResponseEntity.ok("Logged out successfully");
	}
}
