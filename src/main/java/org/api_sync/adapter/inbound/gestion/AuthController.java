package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtil jwtUtil;
	private final Set<String> refreshTokens = new HashSet<>();
	private final UsuarioService usuarioService;
	
	//TOOD: Remover cuando estemos seguros que funciona todo.
	@GetMapping("/verify-token")
	public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
		log.info("Recibida solicitud para verificar token");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Header de autorización vacío o mal formateado: {}", authHeader);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Header de autorización inválido");
		}
		
		String token = authHeader.substring(7);
		log.info("Token extraído: {}", token.substring(0, Math.min(10, token.length())) + "...");
		
		try {
			if (jwtUtil.validateToken(token)) {
				String username = jwtUtil.getUsername(token);
				String pcName = jwtUtil.getPcName(token);
				
				Map<String, Object> tokenInfo = new HashMap<>();
				tokenInfo.put("valid", true);
				tokenInfo.put("username", username);
				tokenInfo.put("pcName", pcName);
				tokenInfo.put("claims", jwtUtil.extractAllClaims(token));
				
				log.info("Token válido para el usuario: {}", username);
				return ResponseEntity.ok(tokenInfo);
			} else {
				log.warn("Token inválido");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
			}
		} catch (Exception e) {
			log.error("Error al verificar el token: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al verificar el token: " + e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("pc_name") String pcName,
			@RequestParam("punto_venta") Integer puntoVenta,
			@RequestParam("empresa_uuid") String empresaUuid,
			@RequestParam("sucursal") Integer sucursalId) {
		
		log.info("Intento de login para usuario: {}, empresa: {}", username, empresaUuid);
		
		// Validar que los campos no estén vacíos
		if (username == null || username.trim().isEmpty() ||
			password == null || password.trim().isEmpty() ||
			empresaUuid == null || empresaUuid.trim().isEmpty() ||
			puntoVenta == null) {
			log.warn("Campos requeridos faltantes en solicitud de login");
			return ResponseEntity.badRequest().body("Todos los campos son requeridos");
		}

		try {
			Optional<Usuario> user = usuarioService.login(username, password, empresaUuid, sucursalId);
			
			if (user.isPresent()) {
				log.info("Login exitoso para usuario: {}", username);
				String accessToken = jwtUtil.generateAccessToken(user.get(), pcName, puntoVenta, empresaUuid, sucursalId);
				String refreshToken = jwtUtil.generateRefreshToken(username);
				refreshTokens.add(refreshToken);
				
				Map<String, String> tokens = new HashMap<>();
				tokens.put("accessToken", accessToken);
				tokens.put("refreshToken", refreshToken);
				
				return ResponseEntity.ok(tokens);
			} else {
				log.warn("Credenciales inválidas para usuario: {}", username);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
			}
		} catch (Exception e) {
			log.error("Error durante el proceso de login para usuario: {}", username, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error durante el proceso de login: " + e.getMessage());
		}
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@RequestParam("refresh_token") String refreshToken) {
		log.info("Solicitud de refresco de token recibida");
		try {
			if (!refreshTokens.contains(refreshToken)) {
				log.warn("Token de refresco inválido o desconocido");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de refresco inválido");
			}

			String username = jwtUtil.getUsername(refreshToken);
			log.info("Refrescando token para usuario: {}", username);
			Usuario user = usuarioService.findBy(username);
			
			String newAccessToken = jwtUtil.generateAccessToken(user, "unknown", 0, "unknown", 0);
			
			Map<String, String> tokens = new HashMap<>();
			tokens.put("accessToken", newAccessToken);
			
			return ResponseEntity.ok(tokens);
		} catch (Exception e) {
			log.error("Error al refrescar el token", e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al refrescar el token: " + e.getMessage());
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestParam String refreshToken) {
		log.info("Solicitud de logout recibida");
		refreshTokens.remove(refreshToken);
		return ResponseEntity.ok("Logged out successfully");
	}
}
