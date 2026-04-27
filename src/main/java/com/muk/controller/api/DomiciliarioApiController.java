package com.muk.controller.api;

import com.muk.service.DomiciliarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/domiciliarios")
@CrossOrigin(origins = "*")
public class DomiciliarioApiController {

    private final DomiciliarioService domiciliarioService;

    @Autowired
    public DomiciliarioApiController(DomiciliarioService domiciliarioService) {
        this.domiciliarioService = domiciliarioService;
    }

    @GetMapping
    public ResponseEntity<List<ApiDtos.DomiciliarioDto>> listarTodos() {
        List<ApiDtos.DomiciliarioDto> domiciliarios = domiciliarioService.findAll()
                .stream()
                .map(ApiMappers::toDomiciliarioDto)
                .toList();

        return ResponseEntity.ok(domiciliarios);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ApiDtos.DomiciliarioDto>> listarDisponibles() {
        List<ApiDtos.DomiciliarioDto> domiciliarios = domiciliarioService.findDisponibles()
                .stream()
                .map(ApiMappers::toDomiciliarioDto)
                .toList();

        return ResponseEntity.ok(domiciliarios);
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        DomiciliarioService.DomiciliarioResult result = domiciliarioService.crear(request);

        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMappers.toDomiciliarioDto(result.domiciliario()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @PathVariable Long id,
            @RequestBody ApiDtos.DomiciliarioUpsertRequest request) {

        DomiciliarioService.DomiciliarioResult result = domiciliarioService.actualizar(id, request);

        if (!result.success()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", result.errorMessage()));
        }

        return ResponseEntity.ok(ApiMappers.toDomiciliarioDto(result.domiciliario()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        String mensaje = domiciliarioService.eliminar(id);

        return ResponseEntity.ok(Map.of("message", mensaje));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Object> activar(@PathVariable Long id) {
        domiciliarioService.activar(id);

        return domiciliarioService.findById(id)
                .<ResponseEntity<Object>>map(d -> ResponseEntity.ok(ApiMappers.toDomiciliarioDto(d)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Domiciliario no encontrado.")));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Object> desactivar(@PathVariable Long id) {
        domiciliarioService.desactivar(id);

        return domiciliarioService.findById(id)
                .<ResponseEntity<Object>>map(d -> ResponseEntity.ok(ApiMappers.toDomiciliarioDto(d)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Domiciliario no encontrado.")));
    }
}