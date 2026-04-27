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
     * Obtiene todos los pedidos con filtros opcionales.
     * GET /api/pedidos
     * GET /api/pedidos?clienteId=X
     * GET /api/pedidos?productoId=X
     */
    @GetMapping
    public ResponseEntity<Object> listaPedidos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long productoId) {

        // Si se proporciona clienteId, devolver solo pedidos de ese cliente
        if (clienteId != null && clienteId > 0) {
            List<ApiDtos.PedidoDto> pedidos = pedidoService.findByClienteId(clienteId)
                    .stream()
                    .map(ApiMappers::toPedidoDto)
                    .toList();
            return ResponseEntity.ok(pedidos);
        }

        // Si se proporciona productoId, filtrar por producto (útil para admin)
        if (productoId != null && productoId > 0) {
            List<ApiDtos.PedidoDto> pedidos = pedidoService.findByProductoId(productoId)
                    .stream()
                    .map(ApiMappers::toPedidoDto)
                    .toList();
            return ResponseEntity.ok(pedidos);
        }

        // Sin filtros: devolver todos los pedidos
        List<ApiDtos.PedidoDto> todosLosPedidos = pedidoService.findAll()
                .stream()
                .map(ApiMappers::toPedidoDto)
                .toList();
        return ResponseEntity.ok(todosLosPedidos);
    }

    /**
     * Actualiza el estado de un pedido (máquina de estados).
     * Al pasar a EN_CAMINO es obligatorio enviar domiciliarioId en el cuerpo.
     * PATCH /api/pedidos/{id}/estado
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Object> actualizarEstado(
            @PathVariable Long id,
            @RequestBody ApiDtos.ActualizarEstadoPedidoRequest request) {

        PedidoService.ActualizarEstadoResult result = pedidoService.actualizarEstado(id, request);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(ApiMappers.toPedidoDto(result.pedido()));
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
}
