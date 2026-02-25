package com.muk.controller;

import com.muk.entities.Producto;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Módulo Administrador: CRUD de Platos.
 *
 * Nota: En el backend la entidad se llama "Producto" (porque así venía el proyecto),
 * pero en la UI y en las rutas lo manejamos como "Plato" para el flujo del restaurante.
 *
 * Regla de la rúbrica: el controlador SOLO habla con el Service (nunca con Repository).
 */
@Controller
@RequestMapping("/admin/platos")
public class AdminPlatoController {

    @Autowired
    private ProductoService productoService;

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

        List<String> categories = productoService.findAll().stream()
                .map(Producto::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("platos", platos);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", q);
        model.addAttribute("currentPage", "admin-platos");
        return "admin/platos/list";
    }

    @GetMapping("/nuevo")
    public String newForm(Model model) {
        model.addAttribute("plato", new Producto());
        model.addAttribute("currentPage", "admin-nuevo");
        return "admin/platos/form";
    }

    /**
     * Un solo endpoint para Create/Update.
     * - Create: plato.id == null
     * - Update: plato.id != null
     */
    @PostMapping("/guardar")
    public String save(@ModelAttribute("plato") Producto plato, RedirectAttributes redirectAttributes) {
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
                .orElse("admin/platos/notfound");
    }

    @GetMapping("/{id}/editar")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "admin-platos");
        return productoService.findById(id)
                .map(p -> {
                    model.addAttribute("plato", p);
                    return "admin/platos/form";
                })
                .orElse("redirect:/admin/platos");
    }

    @GetMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Plato eliminado.");
        return "redirect:/admin/platos";
    }
}
