package com.muk.controller.api;

import com.muk.service.DomiciliarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de domiciliarios.
 * Expone operaciones CRUD y consulta de disponibilidad.
 */
@RestController
@RequestMapping("/api/domiciliarios")
@CrossOrigin(origins = "*")
public class DomiciliarioApiController {

    private final DomiciliarioService domiciliarioService;

    @Autowired
    public DomiciliarioApiController(DomiciliarioService domiciliarioService) {
        this.domiciliarioService = domiciliarioService;
    }

    /**
     * Lista todos los domiciliarios del sistema.
     * GET /api/domiciliarios
     */
    @GetMapping
    public ResponseEntity<List<ApiDtos.DomiciliarioDto>> listar() {
        List<ApiDtos.DomiciliarioDto> lista = domiciliarioService.findAll()
                .stream()
                .map(ApiMappers::toDomiciliarioDto)
                .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Lista solo los domiciliarios disponibles para asignar a un envío.
     * GET /api/domiciliarios/disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<ApiDtos.DomiciliarioDto>> listarDisponibles() {
        List<ApiDtos.DomiciliarioDto> lista = domiciliarioService.findDisponibles()
                .stream()
                .map(ApiMappers::toDomiciliarioDto)
                .toList();
        return ResponseEntity.ok(lista);
    }

    /**
     * Registra un nuevo domiciliario.
     * POST /api/domiciliarios
     */
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        DomiciliarioService.DomiciliarioResult result = domiciliarioService.crear(request);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMappers.toDomiciliarioDto(result.domiciliario()));
    }

    /**
     * Actualiza los datos de un domiciliario.
     * PUT /api/domiciliarios/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @PathVariable Long id,
            @RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        DomiciliarioService.DomiciliarioResult result = domiciliarioService.actualizar(id, request);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(ApiMappers.toDomiciliarioDto(result.domiciliario()));
    }

    /**
     * Desactiva un domiciliario (no disponible / no trabaja hoy).
     * DELETE /api/domiciliarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        domiciliarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activa un domiciliario (vuelve a estar disponible).
     * PATCH /api/domiciliarios/{id}/activar
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Object> activar(@PathVariable Long id) {
        domiciliarioService.activar(id);
        return domiciliarioService.findById(id)
                .<ResponseEntity<Object>>map(d -> ResponseEntity.ok(ApiMappers.toDomiciliarioDto(d)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
