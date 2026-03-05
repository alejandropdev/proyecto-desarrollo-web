package com.muk.controller;

import com.muk.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador MVC para listar y crear categorías.
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String list(Model model) {
        List<String> categories = categoriaService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", "categorias");
        return "categorias/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("currentPage", "categorias");
        return "categorias/form";
    }

    @PostMapping
    public String save(@RequestParam String name, RedirectAttributes redirectAttributes) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre de categoría es obligatorio.");
            return "redirect:/categorias/new";
        }

        categoriaService.addIfMissing(normalized);
        redirectAttributes.addFlashAttribute("message", "Categoría registrada.");
        return "redirect:/categorias";
    }
}
