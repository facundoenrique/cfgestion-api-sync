package org.api_sync.adapter.inbound.gestion;

import io.jsonwebtoken.JwtException;
import org.api_sync.adapter.inbound.gestion.utils.JwtUtil;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.services.usuarios.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;
        String expectedAccessToken = "test.access.token";
        String expectedRefreshToken = "test.refresh.token";

        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        usuario.setPassword(password);

        when(usuarioService.login(username, password)).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateAccessToken(any(Usuario.class), eq(pcName), eq(puntoVenta))).thenReturn(expectedAccessToken);
        when(jwtUtil.generateRefreshToken(username)).thenReturn(expectedRefreshToken);

        // Act
        ResponseEntity<Map<String, String>> response = authController.login(username, password, pcName, puntoVenta);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> tokens = response.getBody();
        assertNotNull(tokens);
        assertEquals(expectedAccessToken, tokens.get("accessToken"));
        assertEquals(expectedRefreshToken, tokens.get("refreshToken"));
        verify(usuarioService, times(1)).login(username, password);
        verify(jwtUtil, times(1)).generateAccessToken(usuario, pcName, puntoVenta);
        verify(jwtUtil, times(1)).generateRefreshToken(username);
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() {
        // Arrange
        String username = "testuser";
        String password = "wrongpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;

        when(usuarioService.login(username, password)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, String>> response = authController.login(username, password, pcName, puntoVenta);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(usuarioService, times(1)).login(username, password);
        verify(jwtUtil, never()).generateAccessToken(any(), anyString(), any());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    void refresh_WithValidToken_ShouldReturnNewAccessToken() {
        // Arrange
        String refreshToken = "valid.refresh.token";
        String username = "testuser";
        String expectedAccessToken = "new.access.token";

        Usuario usuario = new Usuario();
        usuario.setNombre(username);

        // First login to add the refresh token to the set
        when(usuarioService.login(username, "password")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateAccessToken(any(Usuario.class), anyString(), any())).thenReturn("access.token");
        when(jwtUtil.generateRefreshToken(username)).thenReturn(refreshToken);
        authController.login(username, "password", "pc", 1);

        when(jwtUtil.getUsername(refreshToken)).thenReturn(username);
        when(usuarioService.findBy(username)).thenReturn(usuario);
        when(jwtUtil.generateAccessToken(any(Usuario.class), eq("unknown"), eq(0))).thenReturn(expectedAccessToken);

        // Act
        ResponseEntity<Map<String, String>> response = authController.refresh(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> tokens = response.getBody();
        assertNotNull(tokens);
        assertEquals(expectedAccessToken, tokens.get("accessToken"));
        verify(jwtUtil, times(1)).getUsername(refreshToken);
        verify(usuarioService, times(1)).findBy(username);
        verify(jwtUtil, times(1)).generateAccessToken(usuario, "unknown", 0);
    }

    @Test
    void refresh_WithTokenNotInSet_ShouldReturnUnauthorized() {
        // Arrange
        String refreshToken = "invalid.refresh.token";

        // Act
        ResponseEntity<Map<String, String>> response = authController.refresh(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(jwtUtil, never()).getUsername(anyString());
        verify(usuarioService, never()).findBy(anyString());
        verify(jwtUtil, never()).generateAccessToken(any(), anyString(), any());
    }

    @Test
    void refresh_WithInvalidJwtToken_ShouldReturnUnauthorized() {
        // Arrange
        String refreshToken = "valid.refresh.token";
        String username = "testuser";

        // First login to add the refresh token to the set
        Usuario usuario = new Usuario();
        usuario.setNombre(username);
        when(usuarioService.login(username, "password")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateAccessToken(any(Usuario.class), anyString(), any())).thenReturn("access.token");
        when(jwtUtil.generateRefreshToken(username)).thenReturn(refreshToken);
        authController.login(username, "password", "pc", 1);

        // Now simulate JWT exception
        when(jwtUtil.getUsername(refreshToken)).thenThrow(new JwtException("Invalid token"));

        // Act
        ResponseEntity<Map<String, String>> response = authController.refresh(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(jwtUtil, times(1)).getUsername(refreshToken);
        verify(usuarioService, never()).findBy(anyString());
        verify(jwtUtil, times(1)).generateAccessToken(any(), anyString(), any());
    }

    @Test
    void logout_ShouldRemoveRefreshToken() {
        // Arrange
        String refreshToken = "valid.refresh.token";

        // Act
        ResponseEntity<String> response = authController.logout(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
    }
} 