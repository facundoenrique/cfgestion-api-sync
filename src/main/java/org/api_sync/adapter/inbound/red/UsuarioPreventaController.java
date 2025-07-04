package org.api_sync.adapter.inbound.red;

import lombok.RequiredArgsConstructor;
import org.api_sync.adapter.inbound.responses.UsuarioPreventaResponseDTO;
import org.api_sync.adapter.inbound.responses.PedidoConItemsDTO;
import org.api_sync.adapter.outbound.entities.EstadoPreventa;
import org.api_sync.services.preventas.UsuarioPreventaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/red/usuario/preventas")
@RequiredArgsConstructor
public class UsuarioPreventaController {

    private final UsuarioPreventaService usuarioPreventaService;

    @GetMapping
    public ResponseEntity<Page<UsuarioPreventaResponseDTO>> findAll(
            @RequestParam(name = "empresa_id") String empresaId,
            @RequestParam(name = "usuario_codigo") Integer usuarioCodigo,
            @RequestParam(required = false, value = "fecha_desde") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate fechaDesde,
            @RequestParam(required = false, value = "fecha_hasta") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate fechaHasta,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoPreventa estado,
            @PageableDefault(size = 10, sort = "fechaFin", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(usuarioPreventaService.listarPreventasConPedidos(
                empresaId, usuarioCodigo, fechaDesde, fechaHasta, proveedorId, nombre, pageable, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPreventaResponseDTO> findById(
            @RequestParam(name = "empresa_id") String empresaId,
            @PathVariable Long id,
            @RequestParam(name = "usuario_codigo") Integer usuarioCodigo) {
        return ResponseEntity.ok(usuarioPreventaService.obtenerPreventaConPedido(empresaId, id, usuarioCodigo));
    }

    @GetMapping("/preventas/{preventaId}/pedidos")
    public ResponseEntity<List<PedidoConItemsDTO>> getPedidosPorPreventa(@PathVariable Long preventaId) {
        return ResponseEntity.ok(usuarioPreventaService.listarPedidosConItemsPorPreventa(preventaId));
    }

} 