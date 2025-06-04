package org.api_sync.adapter.inbound.red;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.Pedido;
import org.api_sync.services.pedidos.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/red/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @RequestParam Long preventaId,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(pedidoService.crearPedido(preventaId, usuarioId));
    }

    @PutMapping("/{pedidoId}/items")
    public ResponseEntity<Pedido> actualizarItem(
            @PathVariable Long pedidoId,
            @RequestParam Long preventaArticuloId,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(pedidoService.actualizarItem(pedidoId, preventaArticuloId, cantidad));
    }

    @PutMapping("/{pedidoId}/no-participar")
    public ResponseEntity<Pedido> marcarNoParticipacion(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.marcarNoParticipacion(pedidoId));
    }

    @PutMapping("/{pedidoId}/participar")
    public ResponseEntity<Pedido> marcarParticipacion(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.marcarParticipacion(pedidoId));
    }

    @PutMapping("/{pedidoId}/confirmar")
    public ResponseEntity<Pedido> confirmarPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.confirmarPedido(pedidoId));
    }

    @GetMapping("/preventa/{preventaId}")
    public ResponseEntity<Page<Pedido>> obtenerPedidosPorPreventa(
            @PathVariable Long preventaId,
            Pageable pageable) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorPreventa(preventaId, pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<Pedido>> obtenerPedidosPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> obtenerPedidoConItems(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoConItems(pedidoId));
    }

    @DeleteMapping("/{pedidoId}/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId) {
        pedidoService.eliminarItem(pedidoId, itemId);
        return ResponseEntity.noContent().build();
    }
} 