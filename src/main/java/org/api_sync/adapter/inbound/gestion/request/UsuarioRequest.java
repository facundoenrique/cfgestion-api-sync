package org.api_sync.adapter.inbound.gestion.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 5, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El UUID de la empresa es obligatorio")
    private String empresa;
    
    private Integer codigo;
} 