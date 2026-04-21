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
}
