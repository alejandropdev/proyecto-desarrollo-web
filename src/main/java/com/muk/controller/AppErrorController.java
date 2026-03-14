package com.muk.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador centralizado para páginas de error.
 */
@Controller
public class AppErrorController implements ErrorController {

    @GetMapping("/errors/notfound")
    public String notFound(
            @RequestParam(required = false) String ref,
            Model model
    ) {
        NotFoundConfig config = getNotFoundConfig(ref);
        model.addAttribute("title", "Recurso no encontrado");
        model.addAttribute("message", config.message());
        model.addAttribute("backUrl", config.backUrl());
        model.addAttribute("backLabel", config.backLabel());
        model.addAttribute("currentPage", config.currentPage());
        return "errors/notfound";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = 500;
        if (statusCode != null) {
            try {
                status = Integer.parseInt(statusCode.toString());
            } catch (NumberFormatException ignored) {
                status = 500;
            }
        }

        if (status == 404) {
            model.addAttribute("title", "Página no encontrada");
            model.addAttribute("message", "La página que buscas no existe o fue movida.");
            model.addAttribute("backUrl", "/");
            model.addAttribute("backLabel", "Volver al inicio");
            model.addAttribute("currentPage", "index");
            return "errors/notfound";
        }

        model.addAttribute("status", status);
        model.addAttribute("title", "Ocurrió un error");
        model.addAttribute("message", "No pudimos completar tu solicitud. Inténtalo nuevamente.");
        model.addAttribute("currentPage", "index");
        return "errors/error";
    }

    private NotFoundConfig getNotFoundConfig(String ref) {
        if ("plato-admin".equals(ref)) {
            return new NotFoundConfig(
                    "El plato solicitado no existe o fue eliminado.",
                    "/admin/platos",
                    "Volver al listado",
                    "admin-platos"
            );
        }
        if ("comida".equals(ref)) {
            return new NotFoundConfig(
                    "La comida solicitada no fue encontrada.",
                    "/menu",
                    "Volver al menú",
                    "menu"
            );
        }
        if ("cliente".equals(ref)) {
            return new NotFoundConfig(
                    "El cliente solicitado no fue encontrado.",
                    "/clientes",
                    "Volver al listado",
                    "index"
            );
        }
        return new NotFoundConfig(
                "El producto solicitado no fue encontrado.",
                "/productos",
                "Volver al listado",
                "index"
        );
    }

    private record NotFoundConfig(
            String message,
            String backUrl,
            String backLabel,
            String currentPage
    ) {
    }
}
