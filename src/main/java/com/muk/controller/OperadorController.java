package com.muk.controller;

import com.muk.entities.Operador;
import com.muk.service.OperadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador CRUD de operadores. Rutas base /operadores.
 */
@Controller
@RequestMapping("/operadores")
public class OperadorController {

    @Autowired
    private OperadorService operadorService;

    @GetMapping
    public String list(Model model) {
        List<Operador> operadores = operadorService.findAll().stream()
                .filter(o -> Boolean.TRUE.equals(o.getActivo()))
                .toList();
        model.addAttribute("operadores", operadores);
        return "operadores/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("operador", new Operador());
        return "operadores/form";
    }

    @PostMapping
    public String save(@ModelAttribute Operador operador, RedirectAttributes redirectAttributes) {
        boolean isNew = (operador.getId() == null);
        
        // Si se está editando y la contraseña está vacía, mantener la contraseña anterior
        if (!isNew && (operador.getContrasenaHash() == null || operador.getContrasenaHash().isEmpty())) {
            operadorService.findById(operador.getId()).ifPresent(existing -> 
                operador.setContrasenaHash(existing.getContrasenaHash())
            );
        }
        
        operadorService.save(operador);
        redirectAttributes.addFlashAttribute("message",
                isNew ? "Operador creado." : "Operador actualizado.");
        return "redirect:/operadores";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        return operadorService.findById(id)
                .map(o -> {
                    model.addAttribute("operador", o);
                    return "operadores/form";
                })
                .orElse("redirect:/operadores");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        operadorService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Operador eliminado.");
        return "redirect:/operadores";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return operadorService.findById(id)
                .map(o -> {
                    model.addAttribute("operador", o);
                    return "operadores/detail";
                })
                .orElse("redirect:/operadores");
    }
}
