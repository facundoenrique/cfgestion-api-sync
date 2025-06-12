package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Pedido;
import org.api_sync.adapter.outbound.entities.PedidoItem;
import org.api_sync.adapter.outbound.entities.Preventa;
import org.api_sync.adapter.outbound.entities.gestion.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    Page<Pedido> findByPreventaId(Long preventaId, Pageable pageable);
    Page<Pedido> findByPreventaIdAndUsuario(Long preventaId, Usuario usuario, Pageable pageable);
    Page<Pedido> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    Page<Pedido> findByPreventaIdAndUsuarioId(Preventa preventa, Usuario usuario, Pageable pageable);
    Pedido findByPreventaAndUsuario(Preventa preventa, Usuario usuario);
    
    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<Pedido> findByIdWithItems(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM PedidoItem pi WHERE pi.pedido.id = :pedidoId AND pi.id = :itemId")
    void deletePedidoItem(@Param("pedidoId") Long pedidoId, @Param("itemId") Long itemId);

    @Query("SELECT pi FROM PedidoItem pi WHERE pi.pedido.id = :pedidoId")
    Page<PedidoItem> findPedidoItems(@Param("pedidoId") Long pedidoId, Pageable pageable);
} 