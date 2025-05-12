package org.api_sync.adapter.inbound.gestion;

import org.api_sync.adapter.inbound.gestion.request.UsuarioRequest;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.services.usuarios.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;
    
    private Principal mockPrincipal;
    private UsuarioRequest usuarioRequest;
    private Usuario usuario;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockPrincipal = new TestingAuthenticationToken("testUser", "credentials");
        
        // Configurar objetos de prueba
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setUuid("empresa-uuid");
        
        usuario = Usuario.builder()
                .id(1L)
                .nombre("testUser")
                .password("encodedPassword")
                .empresa(empresa)
                .build();
                
        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNombre("testUser");
        usuarioRequest.setPassword("password123");
        usuarioRequest.setEmpresa("empresa-uuid");
    }

    @Test
    void crearUsuario_ShouldReturnCreatedUsuario() {
        // Arrange
        when(usuarioService.crearUsuario(
                usuarioRequest.getNombre(),
                usuarioRequest.getPassword(),
                usuarioRequest.getEmpresa()
        )).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = usuarioController.crearUsuario(usuarioRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
        verify(usuarioService, times(1)).crearUsuario(
                usuarioRequest.getNombre(),
                usuarioRequest.getPassword(),
                usuarioRequest.getEmpresa()
        );
    }

    @Test
    void actualizarUsuario_ShouldReturnUpdatedUsuario() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioService.actualizarUsuario(
                usuarioId,
                usuarioRequest.getNombre(),
                usuarioRequest.getPassword(),
                usuarioRequest.getEmpresa()
        )).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = usuarioController.actualizarUsuario(usuarioId, usuarioRequest);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
        verify(usuarioService, times(1)).actualizarUsuario(
                usuarioId,
                usuarioRequest.getNombre(),
                usuarioRequest.getPassword(),
                usuarioRequest.getEmpresa()
        );
    }

    @Test
    void obtenerTodosLosUsuarios_ShouldReturnAllUsuarios() {
        // Arrange
        Usuario usuario2 = Usuario.builder()
                .id(2L)
                .nombre("otroUsuario")
                .password("encodedPassword2")
                .empresa(empresa)
                .build();
                
        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        // Act
        ResponseEntity<List<Usuario>> response = usuarioController.obtenerTodosLosUsuarios();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuarios, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(usuarioService, times(1)).obtenerTodosLosUsuarios();
    }

    @Test
    void obtenerUsuarioPorId_WhenUsuarioExists_ShouldReturnUsuario() {
        // Arrange
        Long usuarioId = 1L;
        when(usuarioService.obtenerUsuarioPorId(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> response = usuarioController.obtenerUsuarioPorId(usuarioId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
        verify(usuarioService, times(1)).obtenerUsuarioPorId(usuarioId);
    }

    @Test
    void obtenerUsuarioPorId_WhenUsuarioDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        Long usuarioId = 999L;
        when(usuarioService.obtenerUsuarioPorId(usuarioId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Usuario> response = usuarioController.obtenerUsuarioPorId(usuarioId);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(usuarioService, times(1)).obtenerUsuarioPorId(usuarioId);
    }

    @Test
    void obtenerUsuarioPorNombre_WhenUsuarioExists_ShouldReturnUsuario() {
        // Arrange
        String nombre = "testUser";
        when(usuarioService.obtenerUsuarioPorNombre(nombre)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> response = usuarioController.obtenerUsuarioPorNombre(nombre);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
        verify(usuarioService, times(1)).obtenerUsuarioPorNombre(nombre);
    }

    @Test
    void obtenerUsuarioPorNombre_WhenUsuarioDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        String nombre = "nonExistentUser";
        when(usuarioService.obtenerUsuarioPorNombre(nombre)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Usuario> response = usuarioController.obtenerUsuarioPorNombre(nombre);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(usuarioService, times(1)).obtenerUsuarioPorNombre(nombre);
    }

    @Test
    void eliminarUsuario_ShouldReturnOk() {
        // Arrange
        Long usuarioId = 1L;
        doNothing().when(usuarioService).eliminarUsuario(usuarioId);

        // Act
        ResponseEntity<Void> response = usuarioController.eliminarUsuario(usuarioId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(usuarioService, times(1)).eliminarUsuario(usuarioId);
    }
} 