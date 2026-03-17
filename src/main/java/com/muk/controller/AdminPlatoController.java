package com.muk.controller;

import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.service.CategoriaService;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/platos")
public class AdminPlatoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            Model model) {

        List<Producto> platos;
        if (q != null && !q.isBlank()) {
            platos = productoService.searchByName(q.trim());
        } else if (category != null && !category.isBlank()) {
            platos = productoService.findByCategory(category.trim());
        } else {
            platos = productoService.findAll();
        }

        model.addAttribute("platos", platos);
        model.addAttribute("categories", categoriaService.findAll());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", q);
        model.addAttribute("currentPage", "admin-platos");
        return "admin/platos/list";
    }

    @GetMapping("/nuevo")
    public String newForm(Model model) {
        Producto plato = new Producto();
        plato.setCategoria(new Categoria()); // importante

        loadCategories(model);
        model.addAttribute("plato", plato);
        return "admin/platos/form";
    }

    @PostMapping("/guardar")
    public String save(@ModelAttribute("plato") Producto plato, RedirectAttributes redirectAttributes) {
        if (plato.getCategoria() != null && plato.getCategoria().getId() == null) {
            plato.setCategoria(null);
        }
        boolean creating = (plato.getId() == null);
        productoService.save(plato);
        redirectAttributes.addFlashAttribute("message", creating ? "Plato creado." : "Plato actualizado.");
        return "redirect:/admin/platos";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "admin-platos");
        return productoService.findById(id)
                .map(p -> {
                    model.addAttribute("plato", p);
                    return "admin/platos/detail";
                })
                .orElse("redirect:/errors/notfound?ref=plato-admin");
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        loadCategories(model);
        return productoService.findById(id)
                .map(p -> {
                    model.addAttribute("plato", p);
                    return "admin/platos/form";
                })
                .orElse("redirect:/admin/platos");
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Plato eliminado.");
        return "redirect:/admin/platos";
    }

    private void loadCategories(Model model) {
        model.addAttribute("categories", categoriaService.findAll()); // lista de Categoria, no String
    }
}
