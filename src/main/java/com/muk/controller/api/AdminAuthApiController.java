package com.muk.controller.api;

import com.muk.entities.Administrador;
import com.muk.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminAuthApiController {
    private final AdministradorRepository administradorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody ApiDtos.AdminLoginRequest request) {
        Optional<Administrador> admin = administradorRepository.findByUsuarioAndContrasenaHash(request.usuario(), request.password());
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuario o contraseña incorrectos"));
        }
        return ResponseEntity.ok(Map.of("message", "Bienvenido administrador", "usuario", admin.get().getUsuario()));
    }
}
