package org.api_sync.services.afip;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CaeErrorMemory {
    public static class ErrorInfo {
        private final int puntoVenta;
        private final int tipo;
        private final String errorMessage;
        private final Long empresaId;
        private final String empresaNombre;

        public ErrorInfo(int puntoVenta, int tipo, String errorMessage, Long empresaId, String empresaNombre) {
            this.puntoVenta = puntoVenta;
            this.tipo = tipo;
            this.errorMessage = errorMessage;
            this.empresaId = empresaId;
            this.empresaNombre = empresaNombre;
        }

        public int getPuntoVenta() { return puntoVenta; }
        
        public int getTipo() { return tipo;}
        
        public String getTipoTraducido() {
            return switch (tipo) {
                case 1 -> "Factura A";
                case 2 -> "Nota de Crédito A";
                case 5 -> "Factura B";
                case 6 -> "Nota de Crédito B";
                case 14 -> "Factura C";
                case 16 -> "Nota de Crédito C";
                default -> String.valueOf(tipo);
            };
        }
        
        public String getErrorMessage() { return errorMessage; }
        
        public Long getEmpresaId() { return empresaId; }
        
        public String getEmpresaNombre() { return empresaNombre; }
    }

    private final Map<String, ErrorInfo> errorMap = new ConcurrentHashMap<>();

    private String key(int puntoVenta, int tipo) {
        return puntoVenta + ":" + tipo;
    }

    public void addError(int puntoVenta, int tipo, String errorMessage, Long empresaId, String empresaNombre) {
        errorMap.put(key(puntoVenta, tipo), new ErrorInfo(puntoVenta, tipo, errorMessage, empresaId, empresaNombre));
    }

    public void clearError(int puntoVenta, int tipo) {
        errorMap.remove(key(puntoVenta, tipo));
    }

    public Collection<ErrorInfo> getAllErrors() {
        return errorMap.values();
    }
    
    /**
     * Obtener errores agrupados por empresa
     */
    public Map<Long, List<ErrorInfo>> getErrorsByEmpresa() {
        return errorMap.values().stream()
            .collect(Collectors.groupingBy(ErrorInfo::getEmpresaId));
    }
    
    /**
     * Obtener errores de una empresa específica
     */
    public List<ErrorInfo> getErrorsByEmpresa(Long empresaId) {
        return errorMap.values().stream()
            .filter(error -> error.getEmpresaId().equals(empresaId))
            .collect(Collectors.toList());
    }
    
    /**
     * Limpiar errores de una empresa específica
     */
    public void clearErrorsByEmpresa(Long empresaId) {
        errorMap.entrySet().removeIf(entry -> entry.getValue().getEmpresaId().equals(empresaId));
    }
} 