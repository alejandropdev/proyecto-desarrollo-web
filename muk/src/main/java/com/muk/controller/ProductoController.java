package com.muk.controller;

import com.muk.entities.Producto;
import com.muk.service.CategoriaService;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador CRUD de productos. Rutas base /productos.
 */
@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            Model model) {
        List<Producto> products;
        if (q != null && !q.isBlank()) {
            products = productoService.searchByName(q.trim());
        } else if (category != null && !category.isBlank()) {
            products = productoService.findByCategory(category.trim());
        } else {
            products = productoService.findAll();
        }
        List<String> categories = categoriaService.findAll();
        model.addAttribute("productos", products);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", q);
        return "productos/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categories", categoriaService.findAll());
        return "productos/form";
    }

    @PostMapping
    public String save(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        productoService.save(producto);
        redirectAttributes.addFlashAttribute("message", producto.getId() == null ? "Producto creado." : "Producto actualizado.");
        return "redirect:/productos";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        return productoService.findById(id)
                .map(p -> {
                    model.addAttribute("producto", p);
                    model.addAttribute("categories", categoriaService.findAll());
                    return "productos/form";
                })
                .orElse("redirect:/productos");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Producto eliminado.");
        return "redirect:/productos";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return productoService.findById(id)
                .map(p -> {
                    model.addAttribute("producto", p);
                    return "productos/detail";
                })
                .orElse("productos/notfound");
    }
}
