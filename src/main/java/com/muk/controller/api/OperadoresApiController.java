package com.muk.controller.api;

import com.muk.entities.Operador;
import com.muk.service.OperadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/operadores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OperadoresApiController {
    private final OperadorService operadorService;

    @GetMapping
    public List<ApiDtos.OperadorDto> operadores() {
        return operadorService.findAll().stream()
                .filter(o -> Boolean.TRUE.equals(o.getActivo()))
                .map(ApiMappers::toOperadorDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> operadorById(@PathVariable Long id) {
        return operadorService.findById(id)
                .filter(o -> Boolean.TRUE.equals(o.getActivo()))
                .<ResponseEntity<?>>map(o -> ResponseEntity.ok(ApiMappers.toOperadorDto(o)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Operador no encontrado.")));
    }

    @PostMapping
    public ResponseEntity<?> createOperador(@RequestBody ApiDtos.OperadorRequest request) {
        Operador operador = new Operador();
        operador.setNombre(request.nombre().trim());
        operador.setUsuario(request.usuario().trim());
        operador.setContrasenaHash(request.contrasena());
        operador.setActivo(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toOperadorDto(operadorService.save(operador)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOperador(@PathVariable Long id, @RequestBody ApiDtos.OperadorRequest request) {
        Optional<Operador> existing = operadorService.findById(id);
        if (existing.isEmpty() || !Boolean.TRUE.equals(existing.get().getActivo())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Operador no encontrado."));
        }
        Operador operador = existing.get();
        operador.setNombre(request.nombre().trim());
        operador.setUsuario(request.usuario().trim());
        if (request.contrasena() != null && !request.contrasena().isBlank()) {
            operador.setContrasenaHash(request.contrasena());
        }
        return ResponseEntity.ok(ApiMappers.toOperadorDto(operadorService.save(operador)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOperador(@PathVariable Long id) {
        operadorService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Operador eliminado."));
    }
}
