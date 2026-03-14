package com.muk.controller;

import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.service.CategoriaService;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PagesController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("currentPage", "index");
        return "index";
    }

    @GetMapping("/menu")
    public String menu(
            Model model,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String category,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String q) {

        List<Producto> foods;
        if (q != null && !q.isBlank()) {
            foods = productoService.searchByName(q.trim());
        } else if (category != null && !category.isBlank()) {
            foods = productoService.findByCategory(category.trim());
        } else {
            foods = productoService.findAll();
        }

        List<Categoria> categories = categoriaService.findAll();

        model.addAttribute("foods", foods);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", q);
        model.addAttribute("currentPage", "menu");
        return "menu";
    }

    @GetMapping("/comida/{id}")
    public String comida(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "menu");
        return productoService.findById(id)
                .map(p -> {
                    model.addAttribute("producto", p);
                    return "comida";
                })
                .orElse("redirect:/errors/notfound?ref=comida");
    }

    @GetMapping("/desafios")
    public String desafios(Model model) {
        model.addAttribute("currentPage", "desafios");
        return "desafios";
    }

    @GetMapping("/ubicacion")
    public String ubicacion(Model model) {
        model.addAttribute("currentPage", "ubicacion");
        return "ubicacion";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("currentPage", "login");
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("currentPage", "registro");
        return "registro";
    }
}