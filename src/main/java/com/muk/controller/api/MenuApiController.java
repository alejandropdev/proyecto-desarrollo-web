package com.muk.controller.api;

import com.muk.entities.Producto;
import com.muk.service.AdicionalService;
import com.muk.service.CategoriaService;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuApiController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final AdicionalService adicionalService;

    @Autowired
    public MenuApiController(
            ProductoService productoService,
            CategoriaService categoriaService,
            AdicionalService adicionalService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.adicionalService = adicionalService;
    }

    @GetMapping
    public ApiDtos.MenuResponse menu(@RequestParam(required = false) String category,
                                     @RequestParam(required = false) String q) {
        List<Producto> productos = productoService.findByFilters(category, q);
        List<ApiDtos.CategoriaDto> categorias = categoriaService.findAll()
                .stream()
                .map(ApiMappers::toCategoriaDto)
                .toList();

        List<ApiDtos.AdicionalDto> adiciones = adicionalService.findForMenuCategory(category).stream()
                .map(ApiMappers::toAdicionalDto)
                .toList();

        return new ApiDtos.MenuResponse(productos.stream().map(ApiMappers::toProductoDto).toList(), categorias, adiciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiDtos.ProductoDto> comidaDetail(@PathVariable Long id) {
        return productoService.findById(id)
                .map(p -> ResponseEntity.ok(ApiMappers.toProductoDto(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
