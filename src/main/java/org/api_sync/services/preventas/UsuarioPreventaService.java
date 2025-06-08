package org.api_sync.services.preventas;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.adapter.inbound.responses.UsuarioPreventaResponseDTO;
import org.api_sync.adapter.outbound.entities.Pedido;
import org.api_sync.adapter.outbound.entities.PedidoItem;
import org.api_sync.services.exceptions.PreventaNotFoundException;
import org.api_sync.services.pedidos.PedidoService;
import org.api_sync.services.preventas.PreventaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioPreventaService {

    private final PreventaService preventaService;
    private final PedidoService pedidoService;

    @Transactional(readOnly = true)
    public Page<UsuarioPreventaResponseDTO> listarPreventasConPedidos(
            Long usuarioId,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long proveedorId,
            String nombre,
            Pageable pageable) {
        
        // Obtener preventas
        Page<PreventaResponseDTO> preventas = preventaService.listar(fechaDesde, fechaHasta, proveedorId, nombre, pageable);

        // Enriquecer con información de pedidos
        return preventas.map(preventa -> enriquecerPreventaConPedido(preventa, usuarioId));
    }

    @Transactional(readOnly = true)
    public UsuarioPreventaResponseDTO obtenerPreventaConPedido(Long preventaId, Long usuarioId) {
        // Obtener preventa
        PreventaResponseDTO preventa = preventaService.getListaPrecio(preventaId);
        
        // Crear DTO de respuesta
        UsuarioPreventaResponseDTO response = enriquecerPreventaConPedido(preventa, usuarioId);

        // Enriquecer con artículos y cantidades pedidas
        response.setArticulos(enriquecerArticulosConCantidadesPedidas(preventa, usuarioId));

        return response;
    }

    private UsuarioPreventaResponseDTO enriquecerPreventaConPedido(PreventaResponseDTO preventa, Long usuarioId) {
        UsuarioPreventaResponseDTO dto = new UsuarioPreventaResponseDTO();
        dto.setId(preventa.getId());
        dto.setNombre(preventa.getNombre());
        dto.setFechaInicio(preventa.getFechaInicio());
        dto.setFechaFin(preventa.getFechaFin());
        dto.setListaBaseId(preventa.getListaBaseId());

        // Buscar pedido del usuario
        Optional<Pedido> pedido = pedidoService.obtenerPedidosPorPreventa(preventa.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .filter(p -> p.getUsuario().getId().equals(usuarioId))
                .findFirst();

        if (pedido.isPresent()) {
            Pedido p = pedido.get();
            dto.setTienePedido(true);
            dto.setEstadoParticipacion(p.getEstadoParticipacion());
            dto.setMontoTotal(calcularMontoTotal(p));
        } else {
            dto.setTienePedido(false);
            dto.setEstadoParticipacion(null);
            dto.setMontoTotal(BigDecimal.ZERO);
        }

        return dto;
    }

    private List<Map<String, Object>> enriquecerArticulosConCantidadesPedidas(PreventaResponseDTO preventa, Long usuarioId) {
        // Buscar pedido del usuario
        Optional<Pedido> pedido = pedidoService.obtenerPedidosPorPreventa(preventa.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .filter(p -> p.getUsuario().getId().equals(usuarioId))
                .findFirst();

        return preventa.getArticulos().stream()
                .map(articulo -> {
                    Map<String, Object> artMap = Map.of(
                        "id", articulo.getId(),
                        "nombre", articulo.getNombre(),
                        "importe", articulo.getImporte(),
                        "iva", articulo.getIva(),
                        "defecto", articulo.getDefecto() !=null ? articulo.getDefecto() : 1,
                        "multiplicador", articulo.getMultiplicador(),
                        "unidades_por_bulto", articulo.getUnidadesPorBulto(),
                        "cantidad_pedida", pedido.map(p -> obtenerCantidadPedida(p, articulo.getId())).orElse(0)
                    );
                    return artMap;
                })
                .collect(Collectors.toList());
    }

    private Integer obtenerCantidadPedida(Pedido pedido, Long articuloId) {
        return pedido.getItems().stream()
                .filter(item -> item.getPreventaArticulo().getId().equals(articuloId))
                .map(PedidoItem::getCantidad)
                .findFirst()
                .orElse(0);
    }

    private BigDecimal calcularMontoTotal(Pedido pedido) {
        return pedido.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getSubtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
} 