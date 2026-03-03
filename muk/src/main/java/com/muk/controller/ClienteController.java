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

    /**
     * GET /login - Muestra formulario de login
     */
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    /**
     * POST /login - Procesa el login buscando el cliente por email
     */
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        RedirectAttributes redirectAttributes, Model model) {
        return clienteService.findByEmail(email)
                .map(cliente -> {
                    // ahora validamos también la contraseña en texto plano
                    if (password != null && password.equals(cliente.getPassword())) {
                        redirectAttributes.addFlashAttribute("message", "¡Bienvenido " + cliente.getNombre() + "!");
                        return "redirect:/clientes/perfil?email=" + java.net.URLEncoder.encode(cliente.getEmail(), java.nio.charset.StandardCharsets.UTF_8);
                    } else {
                        model.addAttribute("error", "Credenciales inválidas.");
                        return "login";
                    }
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Email no registrado.");
                    return "login";
                });
    }

    /**
     * GET /registro - Muestra formulario de registro
     */
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "registro";
    }

    /**
     * POST /registro - Registra un nuevo cliente
     */
    @PostMapping("/registro")
    public String registro(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        if (cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El email es requerido.");
            return "redirect:/clientes/registro";
        }
        if (cliente.getPassword() == null || cliente.getPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La contraseña es requerida.");
            return "redirect:/clientes/registro";
        }

        // Verificar si el email ya existe
        if (clienteService.findByEmail(cliente.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El email ya está registrado.");
            return "redirect:/clientes/registro";
        }

        clienteService.registro(cliente);
        redirectAttributes.addFlashAttribute("message", "¡Cuenta creada! Ahora inicia sesión.");
        return "redirect:/clientes/login";
    }

    /**
     * GET /perfil - Muestra la página de perfil del usuario (identificado por email en query).
     * Sin sesión: se pasa el email como parámetro tras el login.
     */
    @GetMapping("/perfil")
    public String perfil(@RequestParam(required = false) String email, Model model, RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tu perfil.");
            return "redirect:/clientes/login";
        }
        return clienteService.findByEmail(email.trim())
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("currentPage", "perfil");
                    return "clientes/perfil";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                    return "redirect:/clientes/login";
                });
    }

    /**
     * GET /perfil/editar - Muestra el formulario para editar el perfil.
     */
    @GetMapping("/perfil/editar")
    public String perfilEditarForm(@RequestParam(required = false) String email, Model model, RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para editar tu perfil.");
            return "redirect:/clientes/login";
        }
        return clienteService.findByEmail(email.trim())
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("currentPage", "perfil");
                    return "clientes/perfil-editar";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                    return "redirect:/clientes/login";
                });
    }

    /**
     * POST /perfil/editar - Actualiza el perfil del cliente.
     */
    @PostMapping("/perfil/editar")
    public String perfilEditar(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        if (cliente.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos.");
            return "redirect:/clientes/login";
        }
        // Si la contraseña viene vacía, mantener la actual
        if (cliente.getPassword() == null || cliente.getPassword().isBlank()) {
            clienteService.findById(cliente.getId()).ifPresent(existing ->
                    cliente.setPassword(existing.getPassword()));
        }
        clienteService.save(cliente);
        redirectAttributes.addFlashAttribute("message", "Perfil actualizado.");
        String email = cliente.getEmail() != null ? cliente.getEmail() : "";
        return "redirect:/clientes/perfil?email=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * GET /perfil/eliminar - Elimina la cuenta del cliente (identificado por email).
     */
    @GetMapping("/perfil/eliminar")
    public String perfilEliminar(@RequestParam(required = false) String email, RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para eliminar tu cuenta.");
            return "redirect:/clientes/login";
        }
        return clienteService.findByEmail(email.trim())
                .map(cliente -> {
                    clienteService.delete(cliente.getId());
                    redirectAttributes.addFlashAttribute("message", "Tu cuenta ha sido eliminada.");
                    return "redirect:/clientes/login";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                    return "redirect:/clientes/login";
                });
    }
}
