package com.muk.controller.api;

import com.muk.service.DomiciliarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller para gestión de Domiciliarios.
 * 
 * Endpoints:
 * GET    /api/domiciliarios              - Listar todos los domiciliarios
 * GET    /api/domiciliarios/{id}       - Obtener un domiciliario por ID
 * GET    /api/domiciliarios/activos/disponibles - Listar domiciliarios activos y disponibles
 * POST   /api/domiciliarios             - Crear nuevo domiciliario
 * PUT    /api/domiciliarios/{id}       - Actualizar domiciliario
 * DELETE /api/domiciliarios/{id}       - Eliminar domiciliario
 * PUT    /api/domiciliarios/{id}/activar   - Activar domiciliario
 * PUT    /api/domiciliarios/{id}/desactivar - Desactivar domiciliario
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
     * Obtiene todos los domiciliarios.
     * GET /api/domiciliarios
     */
    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        List<ApiDtos.DomiciliarioDto> domiciliarios = domiciliarioService.findAll()
                .stream()
                .map(ApiMappers::toDomiciliarioDto)
                .toList();
        return ResponseEntity.ok(domiciliarios);
    }

    /**
     * Obtiene un domiciliario por su ID.
     * GET /api/domiciliarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerPorId(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del domiciliario es inválido."));
        }

        return domiciliarioService.findById(id)
                .<ResponseEntity<Object>>map(domiciliario -> ResponseEntity.ok(
                        ApiMappers.toDomiciliarioDto(domiciliario)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Domiciliario no encontrado.")));
    }

    /**
     * Obtiene todos los domiciliarios que están activos Y disponibles.
     * GET /api/domiciliarios/activos/disponibles
     */
    @GetMapping("/activos/disponibles")
    public ResponseEntity<Object> obtenerActivosDisponibles() {
        List<ApiDtos.DomiciliarioDto> domiciliarios = domiciliarioService.findAllActivosDisponibles()
                .stream()
                .map(ApiMappers::toDomiciliarioDto)
                .toList();
        return ResponseEntity.ok(domiciliarios);
    }

    /**
     * Crea un nuevo domiciliario.
     * POST /api/domiciliarios
     * Body: { nombre, celular, cedula }
     */
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        // Validar datos
        if (request.nombre() == null || request.nombre().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El nombre del domiciliario es requerido."));
        }

        if (request.celular() == null || request.celular().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El celular del domiciliario es requerido."));
        }

        if (request.cedula() == null || request.cedula().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "La cédula del domiciliario es requerida."));
        }

        try {
            // Crear entidad Domiciliario
            com.muk.entities.Domiciliario nuevoDomiciliario = new com.muk.entities.Domiciliario();
            nuevoDomiciliario.setNombre(request.nombre());
            nuevoDomiciliario.setCelular(request.celular());
            nuevoDomiciliario.setCedula(request.cedula());

            // Guardar usando el servicio
            com.muk.entities.Domiciliario domiciliarioGuardado = domiciliarioService.crear(nuevoDomiciliario);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiMappers.toDomiciliarioDto(domiciliarioGuardado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Actualiza un domiciliario existente.
     * PUT /api/domiciliarios/{id}
     * Body: { nombre, celular, cedula }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @PathVariable Long id,
            @RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del domiciliario es inválido."));
        }

        try {
            com.muk.entities.Domiciliario datosActualizados = new com.muk.entities.Domiciliario();
            datosActualizados.setNombre(request.nombre());
            datosActualizados.setCelular(request.celular());
            datosActualizados.setCedula(request.cedula());

            com.muk.entities.Domiciliario domiciliarioActualizado = domiciliarioService.actualizar(id, datosActualizados);

            return ResponseEntity.ok(ApiMappers.toDomiciliarioDto(domiciliarioActualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Elimina un domiciliario.
     * Si tiene pedidos asociados, se desactiva (soft delete).
     * DELETE /api/domiciliarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del domiciliario es inválido."));
        }

        try {
            domiciliarioService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Domiciliario eliminado correctamente."));
        } catch (IllegalArgumentException e) {
            // Si el mensaje contiene "pedidos asociados", es un soft delete
            if (e.getMessage().contains("pedidos asociados")) {
                return ResponseEntity.ok(Map.of(
                        "message", "Domiciliario desactivado porque tiene pedidos asociados.",
                        "tipo", "soft-delete"
                ));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al eliminar el domiciliario."));
        }
    }

    /**
     * Activa un domiciliario (marca como activo=true).
     * PUT /api/domiciliarios/{id}/activar
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<Object> activar(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del domiciliario es inválido."));
        }

        try {
            com.muk.entities.Domiciliario domiciliarioActivado = domiciliarioService.activar(id);
            return ResponseEntity.ok(ApiMappers.toDomiciliarioDto(domiciliarioActivado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Desactiva un domiciliario (marca como activo=false).
     * PUT /api/domiciliarios/{id}/desactivar
     */
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Object> desactivar(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El ID del domiciliario es inválido."));
        }

        try {
            com.muk.entities.Domiciliario domiciliarioDesactivado = domiciliarioService.desactivar(id);
            return ResponseEntity.ok(ApiMappers.toDomiciliarioDto(domiciliarioDesactivado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
