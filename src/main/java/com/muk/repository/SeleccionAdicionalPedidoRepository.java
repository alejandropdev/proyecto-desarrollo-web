package com.muk.repository;

import com.muk.entities.SeleccionAdicionalPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeleccionAdicionalPedidoRepository extends JpaRepository<SeleccionAdicionalPedido, Long> {

    List<SeleccionAdicionalPedido> findByItemPedidoId(Long itemPedidoId);
}
