package org.api_sync.services.alertas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.gestion.EmpresaEmailAlerta;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaEmailAlertaRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.afip.MailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaEmailAlertaService {
    
    private final EmpresaEmailAlertaRepository empresaEmailAlertaRepository;
    private final EmpresaRepository empresaRepository;
    private final MailService mailService;
    
    /**
     * Agregar una nueva alerta de email para una empresa
     */
    @Transactional
    public EmpresaEmailAlerta agregarAlerta(Long empresaId, String email, String nombreContacto, 
                                          EmpresaEmailAlerta.TipoAlerta tipoAlerta, String descripcion) {
        
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + empresaId));
        
        // Verificar si ya existe una alerta para esta empresa, email y tipo
        if (empresaEmailAlertaRepository.existsByEmpresaIdAndEmailAndTipoAlertaAndActivoTrue(empresaId, email, tipoAlerta)) {
            throw new RuntimeException("Ya existe una alerta activa para esta empresa, email y tipo");
        }
        
        EmpresaEmailAlerta alerta = EmpresaEmailAlerta.builder()
            .empresa(empresa)
            .email(email)
            .nombreContacto(nombreContacto)
            .tipoAlerta(tipoAlerta)
            .descripcion(descripcion)
            .activo(true)
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        EmpresaEmailAlerta alertaGuardada = empresaEmailAlertaRepository.save(alerta);
        log.info("Alerta agregada para empresa {}: {} - {}", empresaId, email, tipoAlerta);
        
        return alertaGuardada;
    }
    
    /**
     * Obtener todas las alertas activas de una empresa
     */
    public List<EmpresaEmailAlerta> obtenerAlertasActivas(Long empresaId) {
        return empresaEmailAlertaRepository.findByEmpresaIdAndActivoTrue(empresaId);
    }
    
    /**
     * Obtener alertas por tipo para una empresa
     */
    public List<EmpresaEmailAlerta> obtenerAlertasPorTipo(Long empresaId, EmpresaEmailAlerta.TipoAlerta tipoAlerta) {
        return empresaEmailAlertaRepository.findByEmpresaIdAndTipoAlertaAndActivoTrue(empresaId, tipoAlerta);
    }
    
    /**
     * Obtener alertas por tipo para todas las empresas
     */
    public List<EmpresaEmailAlerta> obtenerAlertasPorTipoGlobal(EmpresaEmailAlerta.TipoAlerta tipoAlerta) {
        return empresaEmailAlertaRepository.findByTipoAlertaAndActivoTrue(tipoAlerta);
    }
    
    /**
     * Desactivar una alerta
     */
    @Transactional
    public void desactivarAlerta(Long alertaId) {
        Optional<EmpresaEmailAlerta> alertaOpt = empresaEmailAlertaRepository.findById(alertaId);
        if (alertaOpt.isPresent()) {
            EmpresaEmailAlerta alerta = alertaOpt.get();
            alerta.desactivar();
            empresaEmailAlertaRepository.save(alerta);
            log.info("Alerta desactivada: {}", alertaId);
        }
    }
    
    /**
     * Reactivar una alerta
     */
    @Transactional
    public void reactivarAlerta(Long alertaId) {
        Optional<EmpresaEmailAlerta> alertaOpt = empresaEmailAlertaRepository.findById(alertaId);
        if (alertaOpt.isPresent()) {
            EmpresaEmailAlerta alerta = alertaOpt.get();
            alerta.reactivar();
            empresaEmailAlertaRepository.save(alerta);
            log.info("Alerta reactivada: {}", alertaId);
        }
    }
    
    /**
     * Eliminar una alerta permanentemente
     */
    @Transactional
    public void eliminarAlerta(Long alertaId) {
        empresaEmailAlertaRepository.deleteById(alertaId);
        log.info("Alerta eliminada: {}", alertaId);
    }
    
    /**
     * Enviar alerta a todos los emails configurados para un tipo específico
     */
    @Transactional
    public void enviarAlerta(EmpresaEmailAlerta.TipoAlerta tipoAlerta, String asunto, String contenido) {
        List<EmpresaEmailAlerta> alertas = empresaEmailAlertaRepository.findByTipoAlertaAndActivoTrue(tipoAlerta);
        
        if (alertas.isEmpty()) {
            log.warn("No hay alertas configuradas para el tipo: {}", tipoAlerta);
            return;
        }
        
        // Agrupar emails por empresa para evitar duplicados
        Set<String> emailsUnicos = alertas.stream()
            .map(EmpresaEmailAlerta::getEmail)
            .collect(Collectors.toSet());
        
        for (String email : emailsUnicos) {
            try {
                mailService.sendMail(email, asunto, contenido);
                log.info("Alerta enviada a {}: {}", email, tipoAlerta);
                
                // Marcar las alertas como activadas
                alertas.stream()
                    .filter(alerta -> alerta.getEmail().equals(email))
                    .forEach(EmpresaEmailAlerta::activar);
                    
            } catch (Exception e) {
                log.error("Error enviando alerta a {}: {}", email, e.getMessage());
            }
        }
        
        // Guardar las fechas de activación
        empresaEmailAlertaRepository.saveAll(alertas);
    }
    
    /**
     * Enviar alerta específica a una empresa
     */
    @Transactional
    public void enviarAlertaEmpresa(Long empresaId, EmpresaEmailAlerta.TipoAlerta tipoAlerta, 
                                   String asunto, String contenido) {
        List<EmpresaEmailAlerta> alertas = empresaEmailAlertaRepository
            .findByEmpresaIdAndTipoAlertaAndActivoTrue(empresaId, tipoAlerta);
        
        if (alertas.isEmpty()) {
            log.warn("No hay alertas configuradas para empresa {} y tipo: {}", empresaId, tipoAlerta);
            return;
        }
        
        for (EmpresaEmailAlerta alerta : alertas) {
            try {
                mailService.sendMail(alerta.getEmail(), asunto, contenido);
                alerta.activar();
                log.info("Alerta enviada a empresa {}: {} - {}", empresaId, alerta.getEmail(), tipoAlerta);
            } catch (Exception e) {
                log.error("Error enviando alerta a empresa {}: {}", empresaId, e.getMessage());
            }
        }
        
        empresaEmailAlertaRepository.saveAll(alertas);
    }
    
    /**
     * Obtener estadísticas de alertas por empresa
     */
    public long contarAlertasActivas(Long empresaId) {
        return empresaEmailAlertaRepository.countAlertasActivasByEmpresa(empresaId);
    }
    
    /**
     * Obtener alertas pendientes (que no se han activado en un período)
     */
    public List<EmpresaEmailAlerta> obtenerAlertasPendientes(LocalDateTime fechaLimite) {
        return empresaEmailAlertaRepository.findAlertasPendientes(fechaLimite);
    }
    
    /**
     * Obtener todos los tipos de alerta disponibles
     */
    public EmpresaEmailAlerta.TipoAlerta[] obtenerTiposAlerta() {
        return EmpresaEmailAlerta.TipoAlerta.values();
    }
} 