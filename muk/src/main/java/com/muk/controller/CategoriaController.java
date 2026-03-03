package com.muk.controller;

import com.muk.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints para consultar y registrar categorías.
 */
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<String> list() {
        return categoriaService.findAll();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestParam String name) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre de categoría es obligatorio."));
        }
        categoriaService.addIfMissing(normalized);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", "Categoría registrada.");
        body.put("category", normalized);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
