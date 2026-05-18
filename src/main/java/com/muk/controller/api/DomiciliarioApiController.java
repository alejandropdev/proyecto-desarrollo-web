package com.muk.controller.api;

import com.muk.dto.DomiciliarioResponseDto;
import com.muk.mapper.DomiciliarioMapper;
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
    private final DomiciliarioMapper domiciliarioMapper;

    @Autowired
    public DomiciliarioApiController(DomiciliarioService domiciliarioService, DomiciliarioMapper domiciliarioMapper) {
        this.domiciliarioService = domiciliarioService;
        this.domiciliarioMapper = domiciliarioMapper;
    }

    @GetMapping
    public ResponseEntity<List<DomiciliarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(domiciliarioMapper.toDtoList(domiciliarioService.findAll()));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<DomiciliarioResponseDto>> listarDisponibles() {
        return ResponseEntity.ok(domiciliarioMapper.toDtoList(domiciliarioService.findDisponibles()));
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        DomiciliarioService.DomiciliarioResult result = domiciliarioService.crear(request);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(domiciliarioMapper.toDto(result.domiciliario()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @PathVariable Long id,
            @RequestBody ApiDtos.DomiciliarioUpsertRequest request) {
        DomiciliarioService.DomiciliarioResult result = domiciliarioService.actualizar(id, request);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(Map.of("message", result.errorMessage()));
        }
        return ResponseEntity.ok(domiciliarioMapper.toDto(result.domiciliario()));
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
                .<ResponseEntity<Object>>map(d -> ResponseEntity.ok(domiciliarioMapper.toDto(d)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Domiciliario no encontrado.")));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Object> desactivar(@PathVariable Long id) {
        domiciliarioService.desactivar(id);
        return domiciliarioService.findById(id)
                .<ResponseEntity<Object>>map(d -> ResponseEntity.ok(domiciliarioMapper.toDto(d)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Domiciliario no encontrado.")));
    }
}
