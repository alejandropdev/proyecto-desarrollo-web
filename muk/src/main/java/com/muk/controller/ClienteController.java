package com.muk.controller;

import com.muk.entities.Cliente;
import com.muk.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador CRUD de clientes. Rutas base /clientes.
 */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String list(Model model) {
        List<Cliente> clientes = clienteService.findAll();
        model.addAttribute("clientes", clientes);
        return "clientes/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @PostMapping
    public String save(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        boolean isNew = (cliente.getId() == null);
        clienteService.save(cliente);
        redirectAttributes.addFlashAttribute("message",
                isNew ? "Cliente creado." : "Cliente actualizado.");
        return "redirect:/clientes";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        return clienteService.findById(id)
                .map(c -> {
                    model.addAttribute("cliente", c);
                    return "clientes/form";
                })
                .orElse("redirect:/clientes");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Cliente eliminado.");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return clienteService.findById(id)
                .map(c -> {
                    model.addAttribute("cliente", c);
                    return "clientes/detail";
                })
                .orElse("clientes/notfound");
    }
}
