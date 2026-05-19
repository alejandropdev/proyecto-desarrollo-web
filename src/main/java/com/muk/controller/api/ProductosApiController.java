package com.muk.controller.api;

import com.muk.entities.Producto;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductosApiController {

    private final ProductoService productoService;

    @Autowired
    public ProductosApiController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ApiDtos.MenuProductoDto> productos(@RequestParam(required = false) String category,
                                                   @RequestParam(required = false) String q) {
        return productoService.findByFilters(category, q).stream().map(ApiMappers::toMenuProductoDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDtos.ProductoDto> productoById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok(ApiMappers.toProductoDto(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<ApiDtos.ProductoDto> createProducto(@RequestBody ApiDtos.ProductoUpsertRequest request) {
        try {
            Producto created = productoService.createProducto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiMappers.toProductoDto(created));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDtos.ProductoDto> updateProducto(@PathVariable Long id, @RequestBody ApiDtos.ProductoUpsertRequest request) {
        try {
            return productoService.updateProducto(id, request)
                    .map(producto -> ResponseEntity.ok(ApiMappers.toProductoDto(producto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiDtos.MessageResponse> deleteProducto(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok(new ApiDtos.MessageResponse("Producto eliminado."));
    }

    /**
     * Obtiene las adiciones permitidas para un producto específico.
     * GET /api/productos/{id}/adiciones-permitidas
     */
    @GetMapping("/{id}/adiciones-permitidas")
    public ResponseEntity<List<ApiDtos.AdicionalDto>> obtenerAdicionalesPermitidos(@PathVariable Long id) {
        List<com.muk.entities.Adicional> adiciones = productoService.obtenerAdicionalesPermitidos(id);
        List<ApiDtos.AdicionalDto> dtos = adiciones.stream()
                .map(ApiMappers::toAdicionalDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }
}
