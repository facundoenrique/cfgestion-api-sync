package org.api_sync.services.afip;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

@Component
public class CaeErrorMemory {
    public static class ErrorInfo {
        private final int puntoVenta;
        private final int tipo;
        private final String errorMessage;

        public ErrorInfo(int puntoVenta, int tipo, String errorMessage) {
            this.puntoVenta = puntoVenta;
            this.tipo = tipo;
            this.errorMessage = errorMessage;
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
    }

    private final Map<String, ErrorInfo> errorMap = new ConcurrentHashMap<>();

    private String key(int puntoVenta, int tipo) {
        return puntoVenta + ":" + tipo;
    }

    public void addError(int puntoVenta, int tipo, String errorMessage) {
        errorMap.put(key(puntoVenta, tipo), new ErrorInfo(puntoVenta, tipo, errorMessage));
    }

    public void clearError(int puntoVenta, int tipo) {
        errorMap.remove(key(puntoVenta, tipo));
    }

    public Collection<ErrorInfo> getAllErrors() {
        return errorMap.values();
    }
} 