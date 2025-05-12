package org.api_sync.adapter.inbound.gestion.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private SecretKey getSigningKey() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateAccessToken(Usuario usuario, String pcName, Integer puntoVenta, String empresaUuid, Integer sucursalId) {
		return Jwts.builder()
				       .setSubject(usuario.getNombre())
				       .claim("pcName", pcName)
				       .claim("punto_venta", puntoVenta)
				       .claim("empresa", usuario.getEmpresa())
				       .claim("sucursal", sucursalId)
				       .claim("empresa_uuid", empresaUuid)
				       .setIssuedAt(new Date())
				       .setExpiration(new Date(System.currentTimeMillis() + expiration))
				       .signWith(getSigningKey(), SignatureAlgorithm.HS256)
				       .compact();
	}
	
	public String generateRefreshToken(String username) {
		return Jwts.builder()
				       .setSubject(username)
				       .setIssuedAt(new Date())
				       .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600_000)) // 7 días
				       .signWith(getSigningKey(), SignatureAlgorithm.HS256)
				       .compact();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
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
				       .setSigningKey(getSigningKey())
				       .build()
				       .parseClaimsJws(token)
				       .getBody();
	}
	
	// Extraer solo el username (subject)
	public String getUsername(String token) {
		return Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
	}

	public String getPcName(String token) {
		return Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.get("pcName", String.class);
	}

}
