package org.api_sync.config;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.api_sync.adapter.inbound.gestion.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();
	
	@Value("${jwt.excluded.paths}")
	private String excludedPathsString;
	
	private List<String> excludedPaths;
	
	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		if (excludedPathsString != null && !excludedPathsString.isEmpty()) {
			excludedPaths = Arrays.asList(excludedPathsString.split(","));
			// Limpiar los paths de espacios en blanco
			excludedPaths = excludedPaths.stream()
				.map(String::trim)
				.toList();
		} else {
			excludedPaths = List.of("/auth/**", "/red/**");
		}
		log.info("Excluded paths for JWT filter: {}", excludedPaths);
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		log.info("JwtFilter - Checking path: {}", path);
		
		// Lista de patrones que no requieren autenticación
		List<String> publicPaths = List.of(
			"/red/",
			"/auth/",
			"/v3/api-docs/",
			"/swagger-ui/",
			"/swagger-ui.html",
			"/api-docs/"
		);
		
		// Verificar si el path comienza con alguno de los patrones públicos
		for (String publicPath : publicPaths) {
			if (path.startsWith(publicPath)) {
				log.info("JwtFilter - Path {} is public (starts with {})", path, publicPath);
				return true;
			}
		}
		
		log.info("JwtFilter - Path {} requires authentication", path);
		return false;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
		
		String path = request.getRequestURI();
		String method = request.getMethod();
		log.debug("Processing request {} {} in JwtFilter", method, path);
		log.debug(">>> JwtFilter activo en {}", request.getRequestURI());
		
		// Mostrar todos los headers para depuración
		if (log.isTraceEnabled()) {
            log.trace("Request headers:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.trace("{}: {}", headerName, request.getHeader(headerName));
            }
        }
		
		String authHeader = request.getHeader("Authorization");
		log.debug("Authorization header: {}", authHeader != null ? "present" : "absent");
		
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Missing or invalid Authorization header for path: {}", path);
			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\":\"No autenticado\",\"message\":\"Header de autorización ausente o inválido\"}");
			return;
		}
		
		String token = authHeader.substring(7);
		log.debug("Token extracted from Authorization header");
		
		try {
			log.debug("Validating token...");
			if (!jwtUtil.validateToken(token)) {
				log.warn("Invalid or expired JWT token for path: {}", path);
				response.setContentType("application/json;charset=UTF-8");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("{\"error\":\"No autenticado\",\"message\":\"Token JWT inválido o expirado\"}");
				return;
			}
			
			String username = jwtUtil.getUsername(token);
			String pcName = jwtUtil.getPcName(token);
			
			log.debug("Authenticating user: {} from PC: {} for path: {}", username, pcName, path);
			
			// Extraer todos los claims para debug
			Claims claims = jwtUtil.extractAllClaims(token);
			log.trace("JWT Claims: {}", claims);
			
			// Establecer la autenticación en el contexto de seguridad con un rol básico y detalles adicionales
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				username,
				null,
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
			);
			
			// Agregar más detalles al objeto de autenticación
			authentication.setDetails(claims);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Authentication set in SecurityContext for user: {} and path: {}", username, path);
			
			request.setAttribute("username", username);
			request.setAttribute("pcName", pcName);
			
			filterChain.doFilter(request, response);
			log.debug("Filter chain completed for path: {}", path);
		} catch (JwtException e) {
			log.error("JWT processing error for path {}: {}", path, e.getMessage());
			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"error\":\"No autenticado\",\"message\":\"Error al procesar el token JWT: " + e.getMessage() + "\"}");
		} catch (Exception e) {
			log.error("Unexpected error during authentication for path {}: {}", path, e.getMessage(), e);
			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("{\"error\":\"Error del servidor\",\"message\":\"Error interno del servidor: " + e.getMessage() + "\"}");
		}
	}

}
