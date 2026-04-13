package com.muk.controller.api;

import com.muk.entities.Producto;
import com.muk.repository.AdicionalRepository;
import com.muk.repository.CategoriaRepository;
import com.muk.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuApiController {
    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final AdicionalRepository adicionalRepository;

    @GetMapping
    public ApiDtos.MenuResponse menu(@RequestParam(required = false) String category,
                                     @RequestParam(required = false) String q) {
        List<Producto> productos;
        if (q != null && !q.isBlank()) {
            productos = productoService.searchByName(q.trim());
        } else if (category != null && !category.isBlank()) {
            productos = productoService.findByCategory(category.trim());
        } else {
            productos = productoService.findAll();
        }

        List<ApiDtos.CategoriaDto> categorias = categoriaRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(ApiMappers::toCategoriaDto)
                .toList();

        List<ApiDtos.AdicionalDto> adiciones = (category != null && !category.isBlank())
                ? adicionalRepository.findByCategoria_NombreIgnoreCaseOrderByNombreAsc(category.trim()).stream()
                .map(ApiMappers::toAdicionalDto)
                .toList()
                : List.of();

        return new ApiDtos.MenuResponse(productos.stream().map(ApiMappers::toProductoDto).toList(), categorias, adiciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> comidaDetail(@PathVariable Long id) {
        return productoService.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(ApiMappers.toProductoDto(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Producto no encontrado.")));
    }
}
