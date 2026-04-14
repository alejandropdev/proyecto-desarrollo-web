package com.muk.service;

import com.muk.entities.Administrador;

import java.util.Optional;

public interface AdministradorService {

    Optional<Administrador> login(String usuario, String password);
}
