package com.muk.service;

import com.muk.entities.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de pedidos.
 */
public interface PedidoService {

    List<Pedido> findAll();

    Optional<Pedido> findById(Long id);

    Pedido save(Pedido pedido);

    void delete(Long id);

    List<Pedido> findByOperadorId(Long operadorId);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstado(String estado);
}
