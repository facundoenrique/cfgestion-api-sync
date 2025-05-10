package org.api_sync.adapter.inbound.gestion.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
	private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public String generateAccessToken(Usuario usuario, String pcName, Integer puntoVenta, String empresaUuid, Integer sucursalId) {
		return Jwts.builder()
				       .setSubject(usuario.getNombre())
				       .claim("pcName", pcName)
				       .claim("punto_venta", puntoVenta)
				       .claim("empresa", usuario.getEmpresa())
				       .claim("sucursal", sucursalId)
				       .claim("empresa_uuid", empresaUuid)
				       .setIssuedAt(new Date())
				       .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 4)) // 4 horas
				       .signWith(key, SignatureAlgorithm.HS256)
				       .compact();
	}
	
	public String generateRefreshToken(String username) {
		return Jwts.builder()
				       .setSubject(username)
				       .setIssuedAt(new Date())
				       .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600_000)) // 7 días
				       .signWith(key, SignatureAlgorithm.HS256)
				       .compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException ex) {
			System.out.println("Token expirado");
		} catch (UnsupportedJwtException ex) {
			System.out.println("Token no soportado");
		} catch (MalformedJwtException ex) {
			System.out.println("Token mal formado");
		} catch (SignatureException ex) {
			System.out.println("Firma no válida");
		} catch (IllegalArgumentException ex) {
			System.out.println("Token vacío o nulo");
		}
		return false;
	}
	
	// Extraer claims si lo necesitás
	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				       .setSigningKey(key)
				       .build()
				       .parseClaimsJws(token)
				       .getBody();
	}
	
	// Extraer solo el username (subject)
	public String getUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String getPcName(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("pcName", String.class);
	}

}
