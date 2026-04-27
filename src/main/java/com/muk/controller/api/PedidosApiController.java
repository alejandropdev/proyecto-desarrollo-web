package com.muk.controller.api;

import com.muk.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidosApiController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidosApiController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Crea un nuevo pedido.
     * POST /api/pedidos
     * Body: { items: [ { productoId, cantidad, adiciones: [ { adicionalId, precio } ] } ] }
     */
    @PostMapping
    public ResponseEntity<Object> crearPedido(
            @RequestParam Long clienteId,
            @RequestBody ApiDtos.CrearPedidoRequest request) {
        
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);
        
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMappers.toPedidoDto(result.pedido()));
    }

    /**
     * Obtiene todos los pedidos (para operarios).
     * GET /api/pedidos
     */
    @GetMapping
    public ResponseEntity<Object> listaPedidos(
            @RequestParam(required = false) Long clienteId) {
        
        // Si se proporciona clienteId, devolver solo pedidos de ese cliente
        if (clienteId != null && clienteId > 0) {
            List<ApiDtos.PedidoDto> pedidos = pedidoService.findByClienteId(clienteId)
                    .stream()
                    .map(ApiMappers::toPedidoDto)
                    .toList();
            return ResponseEntity.ok(pedidos);
        }
        
        // Si no hay clienteId, devolver todos los pedidos (para operarios)
        List<ApiDtos.PedidoDto> todosLosPedidos = pedidoService.findAll()
                .stream()
                .map(ApiMappers::toPedidoDto)
                .toList();
        return ResponseEntity.ok(todosLosPedidos);
    }

    /**
     * Obtiene el detalle completo de un pedido específico.
     * GET /api/pedidos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerPedidoDetalle(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del pedido es inválido."));
        }
        
        return pedidoService.findById(id)
                .<ResponseEntity<Object>>map(pedido -> ResponseEntity.ok(
                        ApiMappers.toPedidoDetalleDto(pedido)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Pedido no encontrado.")));
    }

    /**
     * Obtiene todos los pedidos NO completados (para operadores).
     * Estos son los pedidos que aún están en proceso: PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO
     * GET /api/pedidos/sin-completar/lista
     */
    @GetMapping("/sin-completar/lista")
    public ResponseEntity<Object> obtenerPedidosNoCompletados() {
        List<ApiDtos.PedidoDto> pedidosNoCompletados = pedidoService.findPedidosNoCompletados()
                .stream()
                .map(ApiMappers::toPedidoDto)
                .toList();
        return ResponseEntity.ok(pedidosNoCompletados);
    }

    /**
     * Cambia el estado de un pedido.
     * 
     * Estados válidos: PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO, COMPLETADO, CANCELADO
     * 
     * Lógica especial:
     * - Si estado = EN_CAMINO y hay domiciliario asignado → domiciliario.disponible = false
     * - Si estado = COMPLETADO y hay domiciliario asignado → domiciliario.disponible = true
     * - Si estado = CANCELADO y hay domiciliario asignado → domiciliario.disponible = true
     * 
     * PUT /api/pedidos/{id}/cambiar-estado
     * Body: { nuevoEstado: "EN_CAMINO" }
     */
    @PutMapping("/{id}/cambiar-estado")
    public ResponseEntity<Object> cambiarEstado(
            @PathVariable Long id,
            @RequestBody ApiDtos.CambiarEstadoPedidoRequest request) {
        
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del pedido es inválido."));
        }

        if (request.nuevoEstado() == null || request.nuevoEstado().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El nuevo estado del pedido es requerido."));
        }

        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(id, request.nuevoEstado());

        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.ok(ApiMappers.toPedidoDto(result.pedido()));
    }

    /**
     * Asigna un domiciliario a un pedido.
     * 
     * El domiciliario debe estar activo y disponible.
     * 
     * PUT /api/pedidos/{id}/asignar-domiciliario
     * Body: { domiciliarioId: 1 }
     */
    @PutMapping("/{id}/asignar-domiciliario")
    public ResponseEntity<Object> asignarDomiciliario(
            @PathVariable Long id,
            @RequestBody ApiDtos.AsignarDomiciliarioRequest request) {
        
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del pedido es inválido."));
        }

        if (request.domiciliarioId() == null || request.domiciliarioId() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del domiciliario es inválido."));
        }

        PedidoService.AsignarDomiciliarioResult result = pedidoService.asignarDomiciliario(id, request.domiciliarioId());

        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.ok(ApiMappers.toPedidoDto(result.pedido()));
    }
}
