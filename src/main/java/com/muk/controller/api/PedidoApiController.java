package com.muk.controller.api;

import com.muk.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoApiController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoApiController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<ApiDtos.PedidoDto>> listaPedidos() {
        return ResponseEntity.ok(
                pedidoService.findAll().stream()
                        .map(ApiMappers::toPedidoDto)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDtos.PedidoDto> pedidoById(@PathVariable Long id) {
        return pedidoService.findById(id)
                .<ResponseEntity<ApiDtos.PedidoDto>>map(p -> ResponseEntity.ok(ApiMappers.toPedidoDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/operador/{operadorId}")
    public ResponseEntity<List<ApiDtos.PedidoDto>> pedidosPorOperador(@PathVariable Long operadorId) {
        return ResponseEntity.ok(
                pedidoService.findByOperadorId(operadorId).stream()
                        .map(ApiMappers::toPedidoDto)
                        .toList()
        );
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ApiDtos.PedidoDto>> pedidosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(
                pedidoService.findByClienteId(clienteId).stream()
                        .map(ApiMappers::toPedidoDto)
                        .toList()
        );
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ApiDtos.PedidoDto>> pedidosPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(
                pedidoService.findByEstado(estado).stream()
                        .map(ApiMappers::toPedidoDto)
                        .toList()
        );
    }
}
