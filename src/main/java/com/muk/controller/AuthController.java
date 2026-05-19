package com.muk.controller;

import com.muk.security.AuthCookieService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthCookieService authCookieService;

    public AuthController(AuthCookieService authCookieService) {
        this.authCookieService = authCookieService;
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        authCookieService.clearTokenCookie(response);
        redirectAttributes.addFlashAttribute("message", "Sesión cerrada correctamente.");
        return "redirect:/login";
    }
}
