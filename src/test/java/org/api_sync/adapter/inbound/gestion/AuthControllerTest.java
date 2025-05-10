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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private String validRefreshToken;
    private String validAccessToken;
    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar datos de prueba comunes
        testUsuario = new Usuario();
        testUsuario.setNombre("testuser");
        testUsuario.setPassword("testpass");
        
        validRefreshToken = "valid.refresh.token";
        validAccessToken = "valid.access.token";
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;
        String empresaUuid = "test-empresa-uuid";

        when(usuarioService.login(username, password, empresaUuid)).thenReturn(Optional.of(testUsuario));
        when(jwtUtil.generateAccessToken(any(Usuario.class), eq(pcName), eq(puntoVenta))).thenReturn(validAccessToken);
        when(jwtUtil.generateRefreshToken(username)).thenReturn(validRefreshToken);

        // Act
        ResponseEntity<?> response = authController.login(username, password, pcName, puntoVenta, empresaUuid);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> tokens = (Map<String, String>) response.getBody();
        assertNotNull(tokens);
        assertEquals(validAccessToken, tokens.get("accessToken"));
        assertEquals(validRefreshToken, tokens.get("refreshToken"));
        verify(usuarioService, times(1)).login(username, password, empresaUuid);
        verify(jwtUtil, times(1)).generateAccessToken(testUsuario, pcName, puntoVenta);
        verify(jwtUtil, times(1)).generateRefreshToken(username);
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() {
        // Arrange
        String username = "testuser";
        String password = "wrongpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;
        String empresaUuid = "test-empresa-uuid";

        when(usuarioService.login(username, password, empresaUuid)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.login(username, password, pcName, puntoVenta, empresaUuid);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());
        verify(usuarioService, times(1)).login(username, password, empresaUuid);
        verify(jwtUtil, never()).generateAccessToken(any(), anyString(), any());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    void login_WithInvalidEmpresa_ShouldReturnUnauthorized() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;
        String invalidEmpresaUuid = "invalid-empresa-uuid";

        when(usuarioService.login(username, password, invalidEmpresaUuid)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.login(username, password, pcName, puntoVenta, invalidEmpresaUuid);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());
        verify(usuarioService, times(1)).login(username, password, invalidEmpresaUuid);
        verify(jwtUtil, never()).generateAccessToken(any(), anyString(), any());
        verify(jwtUtil, never()).generateRefreshToken(anyString());
    }

    @Test
    void login_WithEmptyFields_ShouldReturnBadRequest() {
        // Arrange
        String username = "";
        String password = "";
        String pcName = "";
        Integer puntoVenta = null;
        String empresaUuid = "";

        // Act
        ResponseEntity<?> response = authController.login(username, password, pcName, puntoVenta, empresaUuid);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Todos los campos son requeridos", response.getBody());
        verify(usuarioService, never()).login(anyString(), anyString(), anyString());
    }

    @Test
    void refresh_WithValidToken_ShouldReturnNewAccessToken() {
        // Arrange
        // Primero hacer login para agregar el refresh token
        String username = "testuser";
        String password = "testpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;
        String empresaUuid = "test-empresa-uuid";

        when(usuarioService.login(username, password, empresaUuid)).thenReturn(Optional.of(testUsuario));
        when(jwtUtil.generateAccessToken(any(Usuario.class), eq(pcName), eq(puntoVenta))).thenReturn(validAccessToken);
        when(jwtUtil.generateRefreshToken(username)).thenReturn(validRefreshToken);

        // Hacer login para agregar el refresh token
        authController.login(username, password, pcName, puntoVenta, empresaUuid);

        // Configurar el refresh
        String newAccessToken = "new.access.token";
        when(jwtUtil.getUsername(validRefreshToken)).thenReturn(username);
        when(usuarioService.findBy(username)).thenReturn(testUsuario);
        when(jwtUtil.generateAccessToken(any(Usuario.class), eq("unknown"), eq(0))).thenReturn(newAccessToken);

        // Act
        ResponseEntity<?> response = authController.refresh(validRefreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> tokens = (Map<String, String>) response.getBody();
        assertNotNull(tokens);
        assertEquals(newAccessToken, tokens.get("accessToken"));
        verify(jwtUtil, times(1)).getUsername(validRefreshToken);
        verify(usuarioService, times(1)).findBy(username);
        verify(jwtUtil, times(1)).generateAccessToken(testUsuario, "unknown", 0);
    }

    @Test
    void refresh_WithInvalidToken_ShouldReturnUnauthorized() {
        // Arrange
        String invalidRefreshToken = "invalid.refresh.token";

        // Act
        ResponseEntity<?> response = authController.refresh(invalidRefreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token de refresco inválido", response.getBody());
        verify(jwtUtil, never()).getUsername(anyString());
        verify(usuarioService, never()).findBy(anyString());
        verify(jwtUtil, never()).generateAccessToken(any(), anyString(), any());
    }

    @Test
    void refresh_WithExpiredToken_ShouldReturnUnauthorized() {
        // Arrange
        String expiredRefreshToken = "expired.refresh.token";
        when(jwtUtil.getUsername(expiredRefreshToken)).thenThrow(new JwtException("Token expirado"));

        // Act
        ResponseEntity<?> response = authController.refresh(expiredRefreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token de refresco inválido", response.getBody());
        verify(jwtUtil, never()).getUsername(expiredRefreshToken);
        verify(usuarioService, never()).findBy(anyString());
        verify(jwtUtil, never()).generateAccessToken(any(), anyString(), any());
    }

    @Test
    void logout_ShouldReturnSuccess() {
        // Arrange
        // Primero hacer login para agregar el refresh token
        String username = "testuser";
        String password = "testpass";
        String pcName = "testpc";
        Integer puntoVenta = 1;
        String empresaUuid = "test-empresa-uuid";

        when(usuarioService.login(username, password, empresaUuid)).thenReturn(Optional.of(testUsuario));
        when(jwtUtil.generateAccessToken(any(Usuario.class), eq(pcName), eq(puntoVenta))).thenReturn(validAccessToken);
        when(jwtUtil.generateRefreshToken(username)).thenReturn(validRefreshToken);

        // Hacer login para agregar el refresh token
        authController.login(username, password, pcName, puntoVenta, empresaUuid);

        // Act
        ResponseEntity<String> response = authController.logout(validRefreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged out successfully", response.getBody());
    }
} 