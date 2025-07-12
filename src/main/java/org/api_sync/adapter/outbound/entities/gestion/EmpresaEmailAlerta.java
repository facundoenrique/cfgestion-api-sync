package org.api_sync.adapter.outbound.entities.gestion;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad para gestionar emails de alerta asociados a empresas
 * Permite configurar diferentes emails para diferentes tipos de advertencias
 */
@Entity
@Table(name = "empresa_email_alertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaEmailAlerta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "nombre_contacto", length = 100)
    private String nombreContacto;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false)
    private TipoAlerta tipoAlerta;
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @Column(name = "fecha_ultima_activacion")
    private LocalDateTime fechaUltimaActivacion;
    
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    /**
     * Tipos de alerta disponibles
     */
    public enum TipoAlerta {
        ERROR_CAE("Errores de CAE"),
        ERROR_SISTEMA("Errores del Sistema"),
        ERROR_BASE_DATOS("Errores de Base de Datos"),
        ALERTA_PERFORMANCE("Alertas de Performance"),
        ALERTA_SEGURIDAD("Alertas de Seguridad"),
        NOTIFICACION_GENERAL("Notificaciones Generales"),
        REPORTE_DIARIO("Reportes Diarios"),
        REPORTE_SEMANAL("Reportes Semanales"),
        REPORTE_MENSUAL("Reportes Mensuales"),
        ALERTA_PEDIDOS("Alertas de Pedidos"),
        ALERTA_PREVENTAS("Alertas de Preventas"),
        ALERTA_CLIENTES("Alertas de Clientes"),
        ALERTA_ARTICULOS("Alertas de Artículos"),
        ALERTA_PRECIOS("Alertas de Precios"),
        ALERTA_CERTIFICADOS("Alertas de Certificados"),
        ALERTA_AFIP("Alertas de AFIP"),
        ALERTA_SINCRONIZACION("Alertas de Sincronización"),
        ALERTA_BACKUP("Alertas de Backup"),
        ALERTA_MANTENIMIENTO("Alertas de Mantenimiento");
        
        private final String descripcion;
        
        TipoAlerta(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    /**
     * Método para activar la alerta (registrar que se envió)
     */
    public void activar() {
        this.fechaUltimaActivacion = LocalDateTime.now();
    }
    
    /**
     * Método para verificar si la alerta está activa
     */
    public boolean estaActiva() {
        return this.activo != null && this.activo;
    }
    
    /**
     * Método para desactivar la alerta
     */
    public void desactivar() {
        this.activo = false;
    }
    
    /**
     * Método para reactivar la alerta
     */
    public void reactivar() {
        this.activo = true;
    }
} 