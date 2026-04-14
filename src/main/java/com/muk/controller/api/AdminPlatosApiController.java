package com.muk.controller.api;

import com.muk.entities.Producto;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/platos")
@CrossOrigin(origins = "*")
public class AdminPlatosApiController {

    private final ProductoService productoService;

    @Autowired
    public AdminPlatosApiController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ApiDtos.ProductoDto> adminPlatos(@RequestParam(required = false) String category,
                                                 @RequestParam(required = false) String q) {
        List<Producto> productos = productoService.findByFilters(category, q);
        return productos.stream().map(ApiMappers::toProductoDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDtos.ProductoDto> adminPlatoById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok(ApiMappers.toProductoDto(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ApiDtos.ProductoDto> createAdminPlato(@RequestBody ApiDtos.ProductoUpsertRequest request) {
        try {
            Producto created = productoService.createProducto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toProductoDto(created));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDtos.ProductoDto> updateAdminPlato(@PathVariable Long id, @RequestBody ApiDtos.ProductoUpsertRequest request) {
        try {
            return productoService.updateProducto(id, request)
                    .map(producto -> ResponseEntity.ok(ApiMappers.toProductoDto(producto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiDtos.MessageResponse> deleteAdminPlato(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok(new ApiDtos.MessageResponse("Producto eliminado."));
    }
}
