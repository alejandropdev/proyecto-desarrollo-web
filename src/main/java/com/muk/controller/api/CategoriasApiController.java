package com.muk.controller.api;

import com.muk.entities.Categoria;
import com.muk.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriasApiController {
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public List<ApiDtos.CategoriaDto> categorias() {
        return categoriaRepository.findAllByOrderByNombreAsc().stream().map(ApiMappers::toCategoriaDto).toList();
    }

    @PostMapping
    public ResponseEntity<?> createCategoria(@RequestBody ApiDtos.CategoriaRequest request) {
        String normalized = request.nombre() == null ? "" : request.nombre().trim();
        if (normalized.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El nombre de categoría es obligatorio."));
        }
        if (categoriaRepository.findByNombreIgnoreCase(normalized).isPresent()) {
            return ResponseEntity.ok(ApiMappers.toCategoriaDto(categoriaRepository.findByNombreIgnoreCase(normalized).get()));
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(normalized);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toCategoriaDto(categoriaRepository.save(categoria)));
    }
}
