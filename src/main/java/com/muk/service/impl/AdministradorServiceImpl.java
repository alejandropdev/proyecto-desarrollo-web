package com.muk.service.impl;

import com.muk.entities.Administrador;
import com.muk.entities.UserEntity;
import com.muk.repository.AdministradorRepository;
import com.muk.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Override
    public LoginResult login(String usuario, String password) {
        if (usuario == null || usuario.isBlank() || password == null || password.isBlank()) {
            return new LoginResult(null, "Usuario o contraseña incorrectos");
        }
        return administradorRepository.findByUsuario(usuario.trim())
                .filter(admin -> {
                    UserEntity user = admin.getUserEntity();
                    return user != null && password.equals(user.getPassword());
                })
                .map(admin -> new LoginResult(admin, null))
                .orElseGet(() -> new LoginResult(null, "Usuario o contraseña incorrectos"));
    }
}
