package com.muk.service;

import com.muk.entities.Administrador;

public interface AdministradorService {

    record LoginResult(Administrador administrador, String errorMessage) {
        public boolean success() {
            return administrador != null;
        }
    }

    LoginResult login(String usuario, String password);
}
    