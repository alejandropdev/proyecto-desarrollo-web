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
                              @RequestParam(required = false) String emailOriginal,
                              RedirectAttributes redirectAttributes) {
        System.out.println("\n=== POST /perfil/editar ===");
        System.out.println("Cliente recibido: " + (cliente != null ? "SÍ" : "NO"));
        if (cliente != null) {
            System.out.println("  ID: " + cliente.getId());
            System.out.println("  Email: " + cliente.getEmail());
            System.out.println("  Nombre: " + cliente.getNombre());
            System.out.println("  Apellido: " + cliente.getApellido());
            System.out.println("  Teléfono: " + cliente.getTelefono());
            System.out.println("  Dirección: " + cliente.getDireccion());
        }
        System.out.println("  Email original (param): " + emailOriginal);
        System.out.println("  Nueva contraseña (param): " + newPassword);
        
        // Validación básica
        if (cliente == null) {
            System.out.println("ERROR: Cliente es null");
            redirectAttributes.addFlashAttribute("error", "Error: Cliente no encontrado.");
            return "redirect:/clientes/login";
        }
        
        // Si no hay ID, obtener el cliente de la base de datos por email original
        if (cliente.getId() == null) {
            String emailBusqueda = emailOriginal != null ? emailOriginal : cliente.getEmail();
            System.out.println("Cliente sin ID, buscando por email: " + emailBusqueda);
            
            if (emailBusqueda == null || emailBusqueda.isBlank()) {
                System.out.println("ERROR: No hay email para buscar");
                redirectAttributes.addFlashAttribute("error", "Email no disponible.");
                return "redirect:/clientes/login";
            }
            
            var existingCliente = clienteService.findByEmail(emailBusqueda.trim());
            if (existingCliente.isPresent()) {
                Cliente dbCliente = existingCliente.get();
                System.out.println("Cliente encontrado en BD. ID: " + dbCliente.getId());
                cliente.setId(dbCliente.getId());
                
                // Si la nueva contraseña está vacía, mantener la anterior
                if (newPassword == null || newPassword.isBlank()) {
                    System.out.println("Preservando contraseña anterior");
                    cliente.setContrasenaHash(dbCliente.getContrasenaHash());
                } else {
                    System.out.println("Actualizando contraseña");
                    cliente.setContrasenaHash(newPassword);
                }
            } else {
                System.out.println("ERROR: Cliente NO encontrado en BD por email: " + emailBusqueda);
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado en el sistema.");
                return "redirect:/clientes/perfil/editar?email=" + java.net.URLEncoder.encode(emailBusqueda, java.nio.charset.StandardCharsets.UTF_8);
            }
        } else {
            // Si ya tiene ID, asegurarse de que la contraseña se mantenga si está vacía
            System.out.println("Cliente YA tiene ID: " + cliente.getId());
            if (newPassword == null || newPassword.isBlank()) {
                System.out.println("Preservando contraseña anterior (cliente con ID)");
                clienteService.findById(cliente.getId()).ifPresent(existing ->
                        cliente.setContrasenaHash(existing.getContrasenaHash()));
            } else {
                System.out.println("Actualizando contraseña (cliente con ID)");
                cliente.setContrasenaHash(newPassword);
            }
        }
        
        System.out.println("Datos listos para guardar. ID: " + cliente.getId() + ", Email: " + cliente.getEmail());
        ClienteService.ActionResult result = clienteService.actualizarPerfil(cliente);
        System.out.println("Respuesta del servicio: éxito=" + result.success() + ", mensaje=" + result.errorMessage());
        
        if (!result.success()) {
            System.out.println("FALLIÓ la actualización. Redirigiendo a editar con error.");
            redirectAttributes.addFlashAttribute("error", result.errorMessage());
            String redirectEmail = cliente.getEmail() != null ? cliente.getEmail() : (emailOriginal != null ? emailOriginal : "");
            String redirectUrl = "redirect:/clientes/perfil/editar?email=" + java.net.URLEncoder.encode(redirectEmail, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("Redirigiendo a: " + redirectUrl);
            return redirectUrl;
        }

        System.out.println("✓ ÉXITO - Perfil actualizado correctamente");
        redirectAttributes.addFlashAttribute("message", "Perfil actualizado exitosamente.");
        String finalEmail = cliente.getEmail() != null ? cliente.getEmail() : "";
        String redirectUrl = "redirect:/clientes/perfil?email=" + java.net.URLEncoder.encode(finalEmail, java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("Redirigiendo EXITOSAMENTE a: " + redirectUrl);
        return redirectUrl;
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
