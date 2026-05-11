package com.muk.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Sirve index.html de la SPA para rutas de cliente Angular, sin interceptar API ni recursos con extensión.
 */
@Component
@Order(Integer.MAX_VALUE - 10)
public class SpaHtml5HistoryFilter extends OncePerRequestFilter {

    private static final Pattern FILE_EXTENSION = Pattern.compile(".+\\.[a-zA-Z0-9]{2,6}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && path.startsWith(context)) {
            path = path.substring(context.length());
        }
        if (path.isEmpty()) {
            path = "/";
        }

        if (path.startsWith("/api") || path.startsWith("/h2-console") || path.startsWith("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        String lastSegment = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        if (!lastSegment.isEmpty() && FILE_EXTENSION.matcher(lastSegment).matches()) {
            filterChain.doFilter(request, response);
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
        dispatcher.forward(request, response);
    }
}
