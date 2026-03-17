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
        ClienteService.LoginResult result = clienteService.login(email, password);
        if (result.success()) {
            Cliente cliente = result.cliente();
            redirectAttributes.addFlashAttribute("message", "¡Bienvenido " + cliente.getNombre() + "!");
            return "redirect:/clientes/perfil?email=" + java.net.URLEncoder.encode(cliente.getEmail(), java.nio.charset.StandardCharsets.UTF_8);
        }

        model.addAttribute("error", result.errorMessage());
        return "login";
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
    ClienteService.RegistroResult result = clienteService.registrarConValidacion(cliente);

    if (!result.success()) {
        redirectAttributes.addFlashAttribute("error", result.errorMessage());
        return "redirect:/clientes/registro";
    }

    redirectAttributes.addFlashAttribute("message", "¡Cuenta creada con éxito!");

    String email = cliente.getEmail() != null ? cliente.getEmail() : "";
    return "redirect:/clientes/perfil?email=" +
            java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
}


    /**
     * GET /perfil - Muestra la página de perfil del usuario (identificado por email en query).
     * Sin sesión: se pasa el email como parámetro tras el login.
     */
    @GetMapping("/perfil")
    public String perfil(@RequestParam(required = false) String email, Model model, RedirectAttributes redirectAttributes) {
        ClienteService.PerfilResult result = clienteService.obtenerPerfilPorEmail(
                email,
                "Debes iniciar sesión para ver tu perfil."
        );
        if (!result.success()) {
            redirectAttributes.addFlashAttribute("error", result.errorMessage());
            return "redirect:/clientes/login";
        }

        model.addAttribute("cliente", result.cliente());
        model.addAttribute("currentPage", "perfil");
        return "clientes/perfil";
    }

    /**
     * GET /perfil/editar - Muestra el formulario para editar el perfil.
     */
    @GetMapping("/perfil/editar")
    public String perfilEditarForm(@RequestParam(required = false) String email, Model model, RedirectAttributes redirectAttributes) {
        ClienteService.PerfilResult result = clienteService.obtenerPerfilPorEmail(
                email,
                "Debes iniciar sesión para editar tu perfil."
        );
        if (!result.success()) {
            redirectAttributes.addFlashAttribute("error", result.errorMessage());
            return "redirect:/clientes/login";
        }

        model.addAttribute("cliente", result.cliente());
        model.addAttribute("currentPage", "perfil");
        return "clientes/perfil-editar";
    }

    /**
     * POST /perfil/editar - Actualiza el perfil del cliente.
     */
    @PostMapping("/perfil/editar")
    public String perfilEditar(@ModelAttribute Cliente cliente, 
                              @RequestParam(required = false) String newPassword,
                              RedirectAttributes redirectAttributes) {
        System.out.println("=== POST /perfil/editar ===");
        System.out.println("Cliente ID: " + cliente.getId());
        System.out.println("Cliente Email: " + cliente.getEmail());
        System.out.println("New Password: " + newPassword);
        
        // Si no hay ID, obtener el cliente de la base de datos por email
        if (cliente.getId() == null && cliente.getEmail() != null) {
            System.out.println("ID es null, buscando por email...");
            var existingCliente = clienteService.findByEmail(cliente.getEmail());
            if (existingCliente.isPresent()) {
                cliente.setId(existingCliente.get().getId());
                System.out.println("Cliente encontrado! ID: " + cliente.getId());
                // Si la nueva contraseña está vacía, mantener la anterior
                if (newPassword == null || newPassword.isBlank()) {
                    cliente.setContrasenaHash(existingCliente.get().getContrasenaHash());
                } else {
                    cliente.setContrasenaHash(newPassword);
                }
            } else {
                System.out.println("Cliente NO encontrado por email");
            }
        }
        
        System.out.println("Actualizando perfil... ID: " + cliente.getId());
        ClienteService.ActionResult result = clienteService.actualizarPerfil(cliente);
        System.out.println("Resultado: " + result.success() + " - " + result.errorMessage());
        
        if (!result.success()) {
            System.out.println("Error al actualizar: " + result.errorMessage());
            redirectAttributes.addFlashAttribute("error", result.errorMessage());
            return "redirect:/clientes/login";
        }

        redirectAttributes.addFlashAttribute("message", "Perfil actualizado.");
        String email = cliente.getEmail() != null ? cliente.getEmail() : "";
        System.out.println("Redirigiendo a perfil con email: " + email);
        return "redirect:/clientes/perfil?email=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * GET /perfil/eliminar - Elimina la cuenta del cliente (identificado por email).
     */
    @GetMapping("/perfil/eliminar")
    public String perfilEliminar(@RequestParam(required = false) String email, RedirectAttributes redirectAttributes) {
        ClienteService.ActionResult result = clienteService.eliminarPerfilPorEmail(
                email,
                "Debes iniciar sesión para eliminar tu cuenta."
        );
        if (!result.success()) {
            redirectAttributes.addFlashAttribute("error", result.errorMessage());
            return "redirect:/clientes/login";
        }

        redirectAttributes.addFlashAttribute("message", "Tu cuenta ha sido eliminada.");
        return "redirect:/clientes/login";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return clienteService.findById(id)
                .map(c -> {
                    model.addAttribute("cliente", c);
                    return "clientes/detail";
                })
                .orElse("redirect:/errors/notfound?ref=cliente");
    }
}
