package org.api_sync.services.pedidos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.*;
import org.api_sync.adapter.outbound.repository.PedidoRepository;
import org.api_sync.adapter.outbound.repository.PreventaArticuloRepository;
import org.api_sync.adapter.outbound.repository.PreventaRepository;
import org.api_sync.adapter.outbound.repository.UsuarioRepository;
import org.api_sync.services.exceptions.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PreventaRepository preventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreventaArticuloRepository preventaArticuloRepository;

    @Transactional
    public Pedido crearPedido(Long preventaId, Long usuarioId) {
        log.info("Creando pedido para preventa {} y usuario {}", preventaId, usuarioId);
        
        Preventa preventa = preventaRepository.findById(preventaId)
            .orElseThrow(() -> new PreventaNotFoundException(preventaId));
            
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        // Verificar si ya existe un pedido para este usuario en esta preventa
        Optional<Pedido> pedidoExistente = pedidoRepository.findByPreventaIdAndUsuarioId(preventaId, usuarioId, Pageable.unpaged())
            .stream()
            .findFirst();

        if (pedidoExistente.isPresent()) {
            return pedidoExistente.get();
        }

        Pedido pedido = new Pedido();
        pedido.setPreventa(preventa);
        pedido.setUsuario(usuario);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstadoParticipacion(EstadoParticipacion.PENDIENTE);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarItem(Long pedidoId, Long preventaArticuloId, Integer cantidad) {
        log.info("Actualizando item {} del pedido {} con cantidad {}", preventaArticuloId, pedidoId, cantidad);
        
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
            
        PreventaArticulo preventaArticulo = preventaArticuloRepository.findById(preventaArticuloId)
            .orElseThrow(() -> new PreventaArticuloNotFoundException(preventaArticuloId));

        // Si el pedido está marcado como NO_PARTICIPA, no permitir agregar items
        if (pedido.getEstadoParticipacion() == EstadoParticipacion.NO_PARTICIPA) {
            throw new IllegalStateException("No se pueden agregar items a un pedido marcado como NO_PARTICIPA");
        }

        // Si la cantidad es 0, eliminamos el item
        if (cantidad == 0) {
            pedido.getItems().removeIf(item -> item.getPreventaArticulo().getId().equals(preventaArticuloId));
            return pedidoRepository.save(pedido);
        }

        // Buscar si el item ya existe en el pedido
        Optional<PedidoItem> itemExistente = pedido.getItems().stream()
            .filter(item -> item.getPreventaArticulo().getId().equals(preventaArticuloId))
            .findFirst();

        if (itemExistente.isPresent()) {
            // Actualizar cantidad existente
            PedidoItem item = itemExistente.get();
            item.setCantidad(cantidad);
            item.setPrecioUnitario(preventaArticulo.getImporte().doubleValue());
            item.setSubtotal(preventaArticulo.getImporte().doubleValue() * cantidad);
        } else {
            // Crear nuevo item
            PedidoItem nuevoItem = new PedidoItem();
            nuevoItem.setPedido(pedido);
            nuevoItem.setPreventaArticulo(preventaArticulo);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(preventaArticulo.getImporte().doubleValue());
            nuevoItem.setSubtotal(preventaArticulo.getImporte().doubleValue() * cantidad);
            pedido.getItems().add(nuevoItem);
        }

        // Actualizar estado de participación
        pedido.setEstadoParticipacion(EstadoParticipacion.PARTICIPA);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido marcarNoParticipacion(Long pedidoId) {
        log.info("Marcando pedido {} como no participante", pedidoId);
        
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        // Limpiar todos los items
        pedido.getItems().clear();
        
        // Actualizar estado
        pedido.setEstadoParticipacion(EstadoParticipacion.NO_PARTICIPA);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido marcarParticipacion(Long pedidoId) {
        log.info("Marcando pedido {} como participante", pedidoId);
        
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        // Actualizar estado
        pedido.setEstadoParticipacion(EstadoParticipacion.PARTICIPA);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido confirmarPedido(Long pedidoId) {
        log.info("Confirmando pedido {}", pedidoId);
        
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
            
        pedido.setFechaConfirmacion(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Page<Pedido> obtenerPedidosPorPreventa(Long preventaId, Pageable pageable) {
        log.info("Obteniendo pedidos para preventa {}", preventaId);
        return pedidoRepository.findByPreventaId(preventaId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Pedido> obtenerPedidosPorUsuario(Long usuarioId, Pageable pageable) {
        log.info("Obteniendo pedidos para usuario {}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedidoConItems(Long pedidoId) {
        log.info("Obteniendo pedido {} con sus items", pedidoId);
        return pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
    }

    @Transactional
    public void eliminarItem(Long pedidoId, Long itemId) {
        log.info("Eliminando item {} del pedido {}", itemId, pedidoId);
        pedidoRepository.deletePedidoItem(pedidoId, itemId);
    }
} 