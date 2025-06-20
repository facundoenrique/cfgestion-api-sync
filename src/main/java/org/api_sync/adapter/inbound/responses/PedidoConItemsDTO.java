package org.api_sync.adapter.inbound.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoConItemsDTO {
    private Long pedidoId;
    private Integer usuarioCodigo;
    private String usuarioNombre;
    private List<ItemPedidoDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        private Long preventaArticuloId;
        private String nombre;
        private BigDecimal importe;
        private Integer cantidadPedida;
    }
} 