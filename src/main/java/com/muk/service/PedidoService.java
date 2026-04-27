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

    /**
     * Obtiene todos los pedidos que aún no están completados.
     * Estados NO completados: PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO
     */
    List<Pedido> findPedidosNoCompletados();

    /**
     * Cambia el estado de un pedido.
     * 
     * Lógica especial:
     * - Si estado = EN_CAMINO → domiciliario.disponible = false
     * - Si estado = COMPLETADO → domiciliario.disponible = true
     * 
     * Estados válidos: PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO, COMPLETADO, CANCELADO
     * 
     * @param pedidoId ID del pedido
     * @param nuevoEstado Estado nuevo (debe ser válido)
     * @return El pedido actualizado
     * @throws IllegalArgumentException si el estado es inválido o el pedido no existe
     */
    record CambiarEstadoResult(Pedido pedido, String errorMessage) {
        public boolean success() {
            return pedido != null;
        }
    }

    CambiarEstadoResult cambiarEstado(Long pedidoId, String nuevoEstado);

    /**
     * Asigna un domiciliario a un pedido.
     * 
     * @param pedidoId ID del pedido
     * @param domiciliarioId ID del domiciliario a asignar
     * @return El pedido actualizado con el domiciliario asignado
     */
    record AsignarDomiciliarioResult(Pedido pedido, String errorMessage) {
        public boolean success() {
            return pedido != null;
        }
    }

    AsignarDomiciliarioResult asignarDomiciliario(Long pedidoId, Long domiciliarioId);

    /**
     * Obtiene los domiciliarios disponibles (activos y con disponible = true)
     */
    // Este método será agregado en la interfaz DomiciliarioService
}
