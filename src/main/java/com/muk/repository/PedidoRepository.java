package com.muk.repository;

import com.muk.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByOperadorId(Long operadorId);

    List<Pedido> findByDomiciliarioId(Long domiciliarioId);

    List<Pedido> findByEstado(String estado);

    /** Obtiene pedidos que contienen al menos un item del producto indicado. */
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.items i WHERE i.producto.id = :productoId")
    List<Pedido> findByProductoId(@Param("productoId") Long productoId);
}
