package com.muk.security.controller;

import com.muk.security.AuthCookieService;
import com.muk.security.dto.LoginUsuario;
import com.muk.security.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController("jwtAuthApiController")
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthCookieService authCookieService;

    @PostMapping("/login")
    public ResponseEntity<AuthSessionResponse> login(
            @RequestBody LoginUsuario loginUsuario,
            HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUsuario.getUsername(), loginUsuario.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);
        authCookieService.addTokenCookie(response, jwt);

        String role = roleFrom(authentication.getAuthorities()).orElse(null);
        AuthSessionResponse session = new AuthSessionResponse(
                authentication.getName(),
                role,
                redirectPathFor(role)
        );

        return new ResponseEntity<>(session, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthSessionResponse> me(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String role = roleFrom(authentication.getAuthorities()).orElse(null);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(new AuthSessionResponse(
                authentication.getName(),
                role,
                redirectPathFor(role)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        authCookieService.clearTokenCookie(response);
        return ResponseEntity.ok(new LogoutResponse("Sesión cerrada correctamente."));
    }

    private Optional<String> roleFrom(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null
                        && authority.startsWith("ROLE_")
                        && !"ROLE_ANONYMOUS".equals(authority))
                .findFirst();
    }

    private String redirectPathFor(String role) {
        if ("ROLE_CLIENTE".equals(role)) {
            return "/clientes/perfil";
        }
        if ("ROLE_ADMIN".equals(role)) {
            return "/admin/platos";
        }
        if ("ROLE_OPERADOR".equals(role)) {
            return "/operario/pedidos";
        }
        return "/";
    }

    public record AuthSessionResponse(String username, String role, String redirectPath) {
    }

    public record LogoutResponse(String message) {
    }
}
