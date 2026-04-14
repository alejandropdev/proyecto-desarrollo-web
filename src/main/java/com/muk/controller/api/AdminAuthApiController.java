package com.muk.controller.api;

import com.muk.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminAuthApiController {

    private final AdministradorService administradorService;

    @Autowired
    public AdminAuthApiController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiDtos.MessageResponse> adminLogin(@RequestBody ApiDtos.AdminLoginRequest request) {
        return administradorService.login(request.usuario(), request.password())
                .map(admin -> ResponseEntity.ok(new ApiDtos.MessageResponse("Bienvenido administrador")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiDtos.MessageResponse("Usuario o contraseña incorrectos")));
    }
}
