package com.muk.service.impl;

import com.muk.entities.Administrador;
import com.muk.repository.AdministradorRepository;
import com.muk.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;

    @Autowired
    public AdministradorServiceImpl(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Override
    public Optional<Administrador> login(String usuario, String password) {
        return administradorRepository.findByUsuarioAndContrasenaHash(usuario, password);
    }
}
