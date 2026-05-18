package com.muk.controller.api;

import com.muk.dto.CategoriaResponseDto;
import com.muk.entities.Categoria;
import com.muk.mapper.CategoriaMapper;
import com.muk.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriasApiController {

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    @Autowired
    public CategoriasApiController(CategoriaService categoriaService, CategoriaMapper categoriaMapper) {
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper;
    }

    @GetMapping
    public List<CategoriaResponseDto> categorias() {
        return categoriaMapper.toDtoList(categoriaService.findAll());
    }

    @PostMapping
    public ResponseEntity<ApiDtos.CategoriaDto> createCategoria(@RequestBody ApiDtos.CategoriaRequest request) {
        try {
            String normalized = request.nombre() == null ? "" : request.nombre().trim();
            boolean existed = categoriaService.findByNombre(normalized).isPresent();
            Categoria categoria = categoriaService.createOrGetByNombre(request.nombre());
            HttpStatus status = existed ? HttpStatus.OK : HttpStatus.CREATED;
            return ResponseEntity.status(status).body(ApiMappers.toCategoriaDto(categoria));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
