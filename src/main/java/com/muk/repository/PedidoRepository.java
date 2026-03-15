package com.muk.repository;

import com.muk.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByOperadorId(Long operadorId);

    List<Pedido> findByDomiciliarioId(Long domiciliarioId);

    List<Pedido> findByEstado(String estado);
}
