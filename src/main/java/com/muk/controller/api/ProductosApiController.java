package com.muk.controller.api;

import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.repository.CategoriaRepository;
import com.muk.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductosApiController {
    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public List<ApiDtos.ProductoDto> productos(@RequestParam(required = false) String category,
                                               @RequestParam(required = false) String q) {
        List<Producto> productos;
        if (q != null && !q.isBlank()) {
            productos = productoService.searchByName(q.trim());
        } else if (category != null && !category.isBlank()) {
            productos = productoService.findByCategory(category.trim());
        } else {
            productos = productoService.findAll();
        }
        return productos.stream().map(ApiMappers::toProductoDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> productoById(@PathVariable Long id) {
        return productoService.findById(id)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(ApiMappers.toProductoDto(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Producto no encontrado.")));
    }

    @PostMapping
    public ResponseEntity<?> createProducto(@RequestBody ApiDtos.ProductoUpsertRequest request) {
        Optional<Categoria> categoria = categoriaRepository.findById(request.categoriaId());
        if (categoria.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Categoría inválida."));
        }
        Producto producto = new Producto();
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion().trim());
        producto.setPrecio(request.precio());
        producto.setImagenUrl(request.imagenUrl().trim());
        producto.setCategoria(categoria.get());
        producto.setActivo(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toProductoDto(productoService.save(producto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody ApiDtos.ProductoUpsertRequest request) {
        Optional<Producto> existing = productoService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Producto no encontrado."));
        }
        Optional<Categoria> categoria = categoriaRepository.findById(request.categoriaId());
        if (categoria.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Categoría inválida."));
        }
        Producto producto = existing.get();
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion().trim());
        producto.setPrecio(request.precio());
        producto.setImagenUrl(request.imagenUrl().trim());
        producto.setCategoria(categoria.get());
        return ResponseEntity.ok(ApiMappers.toProductoDto(productoService.save(producto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado."));
    }
}
