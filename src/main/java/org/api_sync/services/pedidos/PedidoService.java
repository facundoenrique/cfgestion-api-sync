package org.api_sync.services.pedidos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.api_sync.adapter.outbound.entities.*;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.api_sync.adapter.outbound.entities.gestion.Usuario;
import org.api_sync.adapter.outbound.repository.*;
import org.api_sync.adapter.outbound.repository.gestion.EmpresaRepository;
import org.api_sync.services.exceptions.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.api_sync.adapter.inbound.responses.PedidoConItemsDTO;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {
    private final EmpresaRepository empresaRepository;
    private final PedidoRepository pedidoRepository;
    private final PreventaRepository preventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PreventaArticuloRepository preventaArticuloRepository;
    private final UsuarioRelacionRepository usuarioRelacionRepository;
    private final ClienteRepository clienteRepository;

    private void validarPropiedadPedido(Pedido pedido, Long usuarioId) {
        if (!pedido.getUsuario().getId().equals(usuarioId)) {
            throw new PedidoNotOwnedException(pedido.getId(), usuarioId);
        }
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
        log.info("Obteniendo pedido {} con items", pedidoId);
        return pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
    }

    @Transactional
    public Pedido crearPedido(Long preventaId, Integer usuarioId, String empresaId) {
        log.info("Creando pedido para preventa {} y usuario {}", preventaId, usuarioId);
        
        Empresa empresa = empresaRepository.findByUuid(empresaId)
                                  .orElseThrow(() -> new EmpresaNotFoundException(empresaId));
        
        Usuario usuario = usuarioRepository.findByCodigoAndEmpresa(usuarioId, empresa)
                                  .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        
        Preventa preventa = preventaRepository.findById(preventaId)
            .orElseThrow(() -> new PreventaNotFoundException(preventaId));
        
        // Verificar si ya existe un pedido para este usuario en esta preventa
        Optional<Pedido> pedidoExistente = pedidoRepository.findByPreventaIdAndUsuario(preventaId, usuario, Pageable.unpaged())
            .stream()
            .findFirst();

        if (pedidoExistente.isPresent()) {
            throw new PedidoAlreadyExistsException(preventaId, usuarioId);
        }

        Pedido pedido = new Pedido();
        pedido.setPreventa(preventa);
        pedido.setUsuario(usuario);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstadoParticipacion(EstadoParticipacion.PENDIENTE);

        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    private Pedido crearPedido(Preventa preventa, Usuario usuario) {
        Pedido pedido = new Pedido();
        pedido.setPreventa(preventa);
        pedido.setUsuario(usuario);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstadoParticipacion(EstadoParticipacion.PENDIENTE);
        
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarItem(Long pedidoId, Long preventaArticuloId, Integer cantidad, Integer usuarioCodigo,
                                 String empresaId) {
        log.info("Actualizando item {} del pedido {} con cantidad {}", preventaArticuloId, pedidoId, cantidad);
        
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
        
        Empresa empresa = empresaRepository.findByUuid(empresaId)
                                  .orElseThrow(() -> new EmpresaNotFoundException(empresaId));
        
        Usuario usuario = usuarioRepository.findByCodigoAndEmpresa(usuarioCodigo, empresa)
                                  .orElseThrow(() -> new UsuarioNotFoundException(usuarioCodigo));
            
        validarPropiedadPedido(pedido, usuario.getId());
            
        PreventaArticulo preventaArticulo = preventaArticuloRepository.findById(preventaArticuloId)
            .orElseThrow(() -> new PreventaArticuloNotFoundException(preventaArticuloId));

        if (!preventaArticulo.getPreventa().getId().equals(pedido.getPreventa().getId())) {
            throw new PreventaArticuloNotInPreventaException(preventaArticuloId, pedido.getPreventa().getId());
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

        pedido.setEstadoParticipacion(EstadoParticipacion.PARTICIPA);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido marcarNoParticipacion(Long pedidoId, Long usuarioId) {
        log.info("Marcando pedido {} como no participante", pedidoId);
        
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));

        validarPropiedadPedido(pedido, usuarioId);

        pedido.setEstadoParticipacion(EstadoParticipacion.NO_PARTICIPA);
        pedido.getItems().clear();
        
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido marcarParticipacion(Long preventaId, Long usuarioId, EstadoParticipacion participa, String empresaId) {
        log.info("Marcando pedido de preventa {} como participante", preventaId);
        
        Preventa preventa = preventaRepository.findById(preventaId)
                 .orElseThrow(() -> new PreventaNotFoundException(preventaId));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        
        Empresa empresa = empresaRepository.findByUuid(empresaId)
                                  .orElseThrow(() -> new EmpresaNotFoundException(empresaId));
        
        Pedido pedido = pedidoRepository.findByPreventaAndUsuario(preventa, usuario)
                                .orElse(crearPedido(preventa, usuario));

        validarPropiedadPedido(pedido, usuarioId);

        pedido.setEstadoParticipacion(participa);
        pedidoRepository.save(pedido);
        return pedido;
    }

    @Transactional
    public Pedido confirmarPedido(Long pedidoId, Long usuarioId) {
        log.info("Confirmando pedido {}", pedidoId);
        
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
            
        validarPropiedadPedido(pedido, usuarioId);
            
        pedido.setFechaConfirmacion(LocalDateTime.now());
        pedido.setEstadoParticipacion(EstadoParticipacion.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void eliminarItem(Long pedidoId, Long itemId, Long usuarioId) {
        log.info("Eliminando item {} del pedido {}", itemId, pedidoId);
        
        Pedido pedido = pedidoRepository.findByIdWithItems(pedidoId)
            .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
            
        validarPropiedadPedido(pedido, usuarioId);
        
        pedido.getItems().removeIf(item -> item.getId().equals(itemId));
        pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidosPorPreventa(Long preventaId) {
        return pedidoRepository.findByPreventaId(preventaId);
    }

    public List<Pedido> findByPreventaId(Long preventaId) {
        return pedidoRepository.findByPreventaId(preventaId);
    }

    public List<PedidoConItemsDTO> listarPedidosConItemsPorPreventa(Long preventaId) {
        List<Pedido> pedidos = findByPreventaId(preventaId);
        return pedidos.stream().map(pedido -> {
            List<PedidoConItemsDTO.ItemPedidoDTO> items = pedido.getItems().stream().map(item ->
                PedidoConItemsDTO.ItemPedidoDTO.builder()
                    .preventaArticuloId(item.getPreventaArticulo().getId())
                    .nombre(item.getPreventaArticulo().getNombre())
                    .importe(item.getPreventaArticulo().getImporte())
                    .cantidadPedida(item.getCantidad())
                    .build()
            ).collect(Collectors.toList());

            // Get the client name through UsuarioRelacion
            String nombreCliente = usuarioRelacionRepository
                .findByUsuarioIdAndTipoRelacion(pedido.getUsuario().getId(), UsuarioRelacion.TipoRelacion.CLIENTE)
                .flatMap(relacion -> clienteRepository.findById(relacion.getEntidadId()))
                .map(Cliente::getRazonSocial)
                .orElse(pedido.getUsuario().getNombre()); // Fallback to user name if client not found

            return PedidoConItemsDTO.builder()
                    .pedidoId(pedido.getId())
                    .usuarioCodigo(pedido.getUsuario().getCodigo())
                    .usuarioNombre(nombreCliente)
                    .items(items)
                    .build();
        }).collect(Collectors.toList());
    }
} 