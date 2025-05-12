package org.api_sync.adapter.inbound.gestion;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.gestion.request.UsuarioRequest;
import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.services.usuarios.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.crearUsuario(
            request.getNombre(),
            request.getPassword(),
            request.getEmpresa()
        );
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable @NotNull(message = "El ID es obligatorio") @Positive(message = "El ID debe ser positivo") Long id,
            @Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.actualizarUsuario(
            id,
            request.getNombre(),
            request.getPassword(),
            request.getEmpresa()
        );
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(
            @PathVariable @NotNull(message = "El ID es obligatorio") @Positive(message = "El ID debe ser positivo") Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Usuario> obtenerUsuarioPorNombre(
            @PathVariable @NotBlank(message = "El nombre es obligatorio") String nombre) {
        return usuarioService.obtenerUsuarioPorNombre(nombre) //Sumar param empresa
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable @NotNull(message = "El ID es obligatorio") @Positive(message = "El ID debe ser positivo") Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }
} 