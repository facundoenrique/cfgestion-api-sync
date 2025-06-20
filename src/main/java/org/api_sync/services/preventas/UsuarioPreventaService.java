package org.api_sync.services.preventas;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.api_sync.adapter.inbound.responses.UsuarioPreventaResponseDTO;
import org.api_sync.adapter.outbound.entities.EstadoPreventa;
import org.api_sync.adapter.outbound.entities.Pedido;
import org.api_sync.adapter.outbound.entities.PedidoItem;
import org.api_sync.adapter.outbound.entities.gestion.Usuario;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.repository.UsuarioRepository;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.exceptions.UsuarioNotFoundException;
import org.api_sync.services.pedidos.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.api_sync.adapter.inbound.responses.PedidoConItemsDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UsuarioPreventaService {

    private final PreventaService preventaService;
    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public Page<UsuarioPreventaResponseDTO> listarPreventasConPedidos(
            String empresaId,
            Integer usuarioCodigo,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Long proveedorId,
            String nombre,
            Pageable pageable,
            EstadoPreventa estado) {
        
        // Obtener preventas
        Page<PreventaResponseDTO> preventas = preventaService.listar(fechaDesde, fechaHasta, proveedorId, nombre, pageable);

        // Enriquecer con información de pedidos
        return preventas.map(preventa -> enriquecerPreventaConPedido(empresaId, preventa, usuarioCodigo));
    }

    @Transactional(readOnly = true)
    public UsuarioPreventaResponseDTO obtenerPreventaConPedido(String empresaId, Long preventaId, Integer usuarioCodigo) {
        // Obtener preventa
        PreventaResponseDTO preventa = preventaService.getListaPrecio(preventaId);
        
        // Crear DTO de respuesta
        UsuarioPreventaResponseDTO response = enriquecerPreventaConPedido(empresaId, preventa, usuarioCodigo);

        // Enriquecer con artículos y cantidades pedidas
        response.setArticulos(enriquecerArticulosConCantidadesPedidas(preventa, usuarioCodigo));

        return response;
    }

    private UsuarioPreventaResponseDTO enriquecerPreventaConPedido(String empresaId,
                                                                   PreventaResponseDTO preventa,
                                                                   Integer usuarioCodigo) {
        
        UsuarioPreventaResponseDTO dto = new UsuarioPreventaResponseDTO();
        dto.setId(preventa.getId());
        dto.setNombre(preventa.getNombre());
        dto.setFechaInicio(preventa.getFechaInicio());
        dto.setFechaFin(preventa.getFechaFin());
        dto.setListaBaseId(preventa.getListaBaseId());
        dto.setProveedor(preventa.getProveedor());

        Empresa empresa = empresaRepository.findByUuid(empresaId)
                                  .orElseThrow(() -> new RuntimeException(""));
        
        Usuario usuario = usuarioRepository.findByCodigoAndEmpresa(usuarioCodigo, empresa)
                                  .orElseThrow(() -> new UsuarioNotFoundException(usuarioCodigo));
        
        // Buscar pedido del usuario
        Optional<Pedido> pedido = pedidoService.obtenerPedidosPorPreventa(preventa.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .filter(p -> p.getUsuario().getCodigo().equals(usuarioCodigo))
                .findFirst();

        if (pedido.isPresent()) {
            Pedido p = pedido.get();
            dto.setTienePedido(true);
            dto.setPedidoId(pedido.get().getId());
            dto.setEstadoParticipacion(p.getEstadoParticipacion());
            dto.setMontoTotal(calcularMontoTotal(p));
            dto.setUnidadesPedidas(calcularUnidadesPedidas(p));
        } else {
            dto.setTienePedido(false);
            dto.setEstadoParticipacion(null);
            dto.setMontoTotal(BigDecimal.ZERO);
            dto.setUnidadesPedidas(0);
        }

        return dto;
    }

    private List<Map<String, Object>> enriquecerArticulosConCantidadesPedidas(PreventaResponseDTO preventa,
                                                                              Integer usuarioCodigo) {
    
        // Buscar pedido del usuario
        Optional<Pedido> pedido = pedidoService.obtenerPedidosPorPreventa(preventa.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .filter(p -> p.getUsuario().getCodigo().equals(usuarioCodigo))
                .findFirst();

        return preventa.getArticulos().stream()
                .map(articulo -> {
                    Map<String, Object> artMap = new HashMap<>();
                    artMap.put("id", articulo.getId());
                    artMap.put("nombre", articulo.getNombre());
                    artMap.put("importe", articulo.getImporte());
                    artMap.put("iva", articulo.getIva());
                    artMap.put("defecto", articulo.getDefecto() != null ? articulo.getDefecto() : 1);
                    artMap.put("multiplicador", articulo.getMultiplicador());
                    artMap.put("unidades_por_bulto", articulo.getUnidadesPorBulto());
                    artMap.put("cantidad_pedida", pedido.map(p -> obtenerCantidadPedida(p, articulo.getId())).orElse(0));
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

    private Integer calcularUnidadesPedidas(Pedido pedido) {
        return pedido.getItems().stream()
                       .map(PedidoItem::getCantidad)
                       .findFirst()
                       .orElse(0);
    }

    private BigDecimal calcularMontoTotal(Pedido pedido) {
        return pedido.getItems().stream()
                .map(item -> BigDecimal.valueOf(item.getSubtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<PedidoConItemsDTO> listarPedidosConItemsPorPreventa(Long preventaId) {
        List<Pedido> pedidos = pedidoService.findByPreventaId(preventaId);
        return pedidos.stream().map(pedido -> {
            List<PedidoConItemsDTO.ItemPedidoDTO> items = pedido.getItems().stream().map(item ->
                PedidoConItemsDTO.ItemPedidoDTO.builder()
                    .preventaArticuloId(item.getPreventaArticulo().getId())
                    .nombre(item.getPreventaArticulo().getNombre())
                    .importe(item.getPreventaArticulo().getImporte())
                    .cantidadPedida(item.getCantidad())
                    .build()
            ).collect(Collectors.toList());
            return PedidoConItemsDTO.builder()
                    .pedidoId(pedido.getId())
                    .usuarioCodigo(pedido.getUsuario().getCodigo())
                    .usuarioNombre(pedido.getUsuario().getNombre())
                    .items(items)
                    .build();
        }).collect(Collectors.toList());
    }

} 