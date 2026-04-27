package com.muk.service;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidoService {

    record CrearPedidoResult(Pedido pedido, String errorMessage) {
        public boolean success() {
            return pedido != null;
        }
    }

    record CambiarEstadoResult(Pedido pedido, String errorMessage) {
        public boolean success() {
            return pedido != null;
        }
    }

    record AsignarDomiciliarioResult(Pedido pedido, String errorMessage) {
        public boolean success() {
            return pedido != null;
        }
    }

    List<Pedido> findAll();

    Optional<Pedido> findById(Long id);

    List<Pedido> findByClienteId(Long clienteId);

    CrearPedidoResult crearPedido(Long clienteId, ApiDtos.CrearPedidoRequest request);

    Pedido save(Pedido pedido);

    void delete(Long id);

    List<Pedido> findByEstado(String estado);

    List<Pedido> findByProductoId(Long productoId);

    List<Pedido> findPedidosNoCompletados();

    CambiarEstadoResult cambiarEstado(Long pedidoId, String nuevoEstado);

    AsignarDomiciliarioResult asignarDomiciliario(Long pedidoId, Long domiciliarioId);
}