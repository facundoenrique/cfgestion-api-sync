package org.api_sync.adapter.inbound.gestion.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.api_sync.adapter.outbound.entities.gestion.Usuario;

@Data
public class UsuarioRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombre;

//    @NotBlank(message = "La contraseña es obligatoria")
//    @Size(min = 5, message = "La contraseña debe tener al menos 6 caracteres") //solo para crear es obligatorio
    private String password;
    private short eliminado;
    
    public Usuario toEntity(int codigo) {
        return Usuario.builder()
                       .codigo(codigo)
                       .password(this.password)
                       .nombre(this.nombre)
                       .eliminado(this.eliminado)
                       .build();
    }
} 