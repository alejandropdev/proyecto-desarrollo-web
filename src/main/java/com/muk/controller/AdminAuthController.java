package com.muk.controller;

import com.muk.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AdministradorRepository administradorRepository;

    @GetMapping("/login")
    public String loginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usuario,
                        @RequestParam String password,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        return administradorRepository.findByUsuarioAndContrasenaHash(usuario, password)
                .map(admin -> {
                    redirectAttributes.addFlashAttribute("message", "Bienvenido administrador");
                    return "redirect:/admin/platos";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Usuario o contraseña incorrectos");
                    return "admin/login";
                });
    }
}
