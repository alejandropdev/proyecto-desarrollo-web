package com.muk.service;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de pedidos. Lógica de negocio.
 */
public interface PedidoService {

    record CrearPedidoResult(Pedido pedido, String errorMessage) {
        public boolean success() {
            return pedido != null;
        }
    }

    /**
     * Obtiene todos los pedidos.
     */
    List<Pedido> findAll();

    /**
     * Obtiene un pedido por su id.
     */
    Optional<Pedido> findById(Long id);

    /**
     * Obtiene los pedidos asociados a un cliente.
     */
    List<Pedido> findByClienteId(Long clienteId);

    /**
     * Crea un nuevo pedido a partir de los datos solicitados.
     * El estado se asigna automáticamente como "PENDIENTE".
     */
    CrearPedidoResult crearPedido(Long clienteId, ApiDtos.CrearPedidoRequest request);

    /**
     * Guarda un pedido existente.
     */
    Pedido save(Pedido pedido);

    /**
     * Elimina un pedido por su id.
     */
    void delete(Long id);

    /**
     * Obtiene los pedidos filtrados por estado.
     */
    List<Pedido> findByEstado(String estado);
}
