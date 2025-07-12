package org.api_sync.adapter.outbound.repository.gestion;

import org.api_sync.adapter.outbound.entities.gestion.EmpresaEmailAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaEmailAlertaRepository extends JpaRepository<EmpresaEmailAlerta, Long> {
    
    /**
     * Buscar todas las alertas activas de una empresa
     */
    List<EmpresaEmailAlerta> findByEmpresaIdAndActivoTrue(Long empresaId);
    
    /**
     * Buscar alertas por empresa y tipo
     */
    List<EmpresaEmailAlerta> findByEmpresaIdAndTipoAlertaAndActivoTrue(Long empresaId, EmpresaEmailAlerta.TipoAlerta tipoAlerta);
    
    /**
     * Buscar alertas por tipo de alerta (todas las empresas)
     */
    List<EmpresaEmailAlerta> findByTipoAlertaAndActivoTrue(EmpresaEmailAlerta.TipoAlerta tipoAlerta);
    
    /**
     * Buscar alertas por email específico
     */
    List<EmpresaEmailAlerta> findByEmailAndActivoTrue(String email);
    
    /**
     * Buscar alerta específica por empresa, email y tipo
     */
    Optional<EmpresaEmailAlerta> findByEmpresaIdAndEmailAndTipoAlerta(Long empresaId, String email, EmpresaEmailAlerta.TipoAlerta tipoAlerta);
    
    /**
     * Verificar si existe una alerta para empresa, email y tipo
     */
    boolean existsByEmpresaIdAndEmailAndTipoAlertaAndActivoTrue(Long empresaId, String email, EmpresaEmailAlerta.TipoAlerta tipoAlerta);
    
    /**
     * Buscar alertas por empresa con paginación
     */
    @Query("SELECT eea FROM EmpresaEmailAlerta eea WHERE eea.empresa.id = :empresaId ORDER BY eea.fechaCreacion DESC")
    List<EmpresaEmailAlerta> findByEmpresaIdOrderByFechaCreacionDesc(@Param("empresaId") Long empresaId);
    
    /**
     * Buscar alertas que no se han activado en un período específico
     */
    @Query("SELECT eea FROM EmpresaEmailAlerta eea WHERE eea.activo = true AND (eea.fechaUltimaActivacion IS NULL OR eea.fechaUltimaActivacion < :fechaLimite)")
    List<EmpresaEmailAlerta> findAlertasPendientes(@Param("fechaLimite") java.time.LocalDateTime fechaLimite);
    
    /**
     * Contar alertas activas por empresa
     */
    @Query("SELECT COUNT(eea) FROM EmpresaEmailAlerta eea WHERE eea.empresa.id = :empresaId AND eea.activo = true")
    long countAlertasActivasByEmpresa(@Param("empresaId") Long empresaId);
    
    /**
     * Buscar alertas por empresa y múltiples tipos
     */
    @Query("SELECT eea FROM EmpresaEmailAlerta eea WHERE eea.empresa.id = :empresaId AND eea.tipoAlerta IN :tipos AND eea.activo = true")
    List<EmpresaEmailAlerta> findByEmpresaIdAndTipoAlertaIn(@Param("empresaId") Long empresaId, @Param("tipos") List<EmpresaEmailAlerta.TipoAlerta> tipos);
} 