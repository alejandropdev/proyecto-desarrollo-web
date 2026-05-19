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

    @PostMapping
    public ResponseEntity<Object> crearPedido(
            @RequestParam Long clienteId,
            @RequestBody ApiDtos.CrearPedidoRequest request) {

        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMappers.toPedidoDto(result.pedido()));
    }

    @GetMapping
    public ResponseEntity<Object> listaPedidos(@RequestParam(required = false) Long clienteId) {
        if (clienteId != null && clienteId > 0) {
            List<ApiDtos.PedidoDto> pedidosCliente = pedidoService.findByClienteId(clienteId)
                    .stream().map(ApiMappers::toPedidoDto).toList();
            return ResponseEntity.ok(pedidosCliente);
        }

        List<ApiDtos.PedidoOperadorDto> pedidosOperario = pedidoService.findAll()
                .stream().map(ApiMappers::toPedidoOperadorDto).toList();

        return ResponseEntity.ok(pedidosOperario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerPedidoDetalle(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del pedido es inválido."));
        }

        return pedidoService.findByIdWithDetails(id)
                .<ResponseEntity<Object>>map(pedido -> ResponseEntity.ok(
                        ApiMappers.toPedidoDetalleDto(pedido)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Pedido no encontrado.")));
    }

    @GetMapping("/sin-completar/lista")
    public ResponseEntity<Object> obtenerPedidosNoCompletados() {
        List<ApiDtos.PedidoOperadorDto> pedidosNoCompletados = pedidoService.findPedidosNoCompletados()
                .stream().map(ApiMappers::toPedidoOperadorDto).toList();

        return ResponseEntity.ok(pedidosNoCompletados);
    }

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

        PedidoService.CambiarEstadoResult result =
                pedidoService.cambiarEstado(id, request.nuevoEstado());

        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.ok(ApiMappers.toPedidoOperadorDto(result.pedido()));
    }

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

        PedidoService.AsignarDomiciliarioResult result =
                pedidoService.asignarDomiciliario(id, request.domiciliarioId());

        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.ok(ApiMappers.toPedidoOperadorDto(result.pedido()));
    }
}
