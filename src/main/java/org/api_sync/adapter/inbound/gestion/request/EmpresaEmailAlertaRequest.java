package org.api_sync.adapter.inbound.gestion.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.api_sync.adapter.outbound.entities.gestion.EmpresaEmailAlerta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO para crear/actualizar alertas de email de empresa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaEmailAlertaRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @NotBlank(message = "El nombre del contacto es obligatorio")
    private String nombreContacto;
    
    @NotNull(message = "El tipo de alerta es obligatorio")
    private EmpresaEmailAlerta.TipoAlerta tipoAlerta;
    
    private String descripcion;
} 