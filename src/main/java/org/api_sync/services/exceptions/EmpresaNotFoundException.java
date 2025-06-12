package org.api_sync.services.exceptions;

public class EmpresaNotFoundException extends RuntimeException {
    public EmpresaNotFoundException(String uuid) {
        super("No se encontró la empresa con uuid: " + uuid);
    }
} 