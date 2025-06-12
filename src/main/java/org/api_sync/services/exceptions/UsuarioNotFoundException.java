package org.api_sync.services.exceptions;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long usuarioId) {
        super("No se encontró el usuario con ID: " + usuarioId);
    }
    public UsuarioNotFoundException(Integer codigo) {
        super("No se encontró el usuario con codigo: " + codigo);
    }
} 