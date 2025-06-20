package org.api_sync.adapter.inbound.red;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.outbound.entities.EstadoParticipacion;
import org.api_sync.adapter.outbound.entities.Pedido;
import org.api_sync.services.pedidos.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/red/usuarios/pedidos")
@RequiredArgsConstructor
public class UsuarioPedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @RequestParam(name = "preventa_id") Long preventaId,
            @RequestParam(name = "empresa_id") String empresaId,
            @RequestParam(name = "usuario_codigo") Integer usuarioCodigo) {
        return ResponseEntity.ok(pedidoService.crearPedido(preventaId, usuarioCodigo, empresaId));
    }

    @PutMapping("/{pedidoId}/items")
    public ResponseEntity<Pedido> actualizarItem(
            @PathVariable Long pedidoId,
            @RequestParam(name="preventa_articulo_id") Long preventaArticuloId,
            @RequestParam Integer cantidad,
            @RequestParam(name="usuario_codigo") Integer usuarioCodigo,
            @RequestParam(name = "empresa_id") String empresaId) {
        return ResponseEntity.ok(pedidoService.actualizarItem(pedidoId, preventaArticuloId, cantidad, usuarioCodigo, empresaId));
    }

    @PutMapping("/{preventaId}/participar")
    public ResponseEntity<Pedido> marcarParticipacion(
            @PathVariable Long preventaId,
            @RequestParam("usuario_id") Long usuarioId,
            @RequestParam EstadoParticipacion participa,
            @RequestParam(name = "empresa_id") String empresaId) {
        pedidoService.marcarParticipacion(preventaId, usuarioId, participa, empresaId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{pedidoId}/confirmar")
    public ResponseEntity<Pedido> confirmarPedido(
            @PathVariable Long pedidoId,
            @RequestParam("usuario_id") Long usuarioId) {
        pedidoService.confirmarPedido(pedidoId, usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preventa/{preventaId}")
    public ResponseEntity<Page<Pedido>> obtenerPedidosPorPreventa(
            @PathVariable Long preventaId,
            @RequestParam String empresaId,
            @RequestParam Integer usuarioCodigo,
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

} 