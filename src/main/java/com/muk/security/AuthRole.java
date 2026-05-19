package com.muk.security;

import java.util.Optional;

public enum AuthRole {
    CLIENTE("/clientes/perfil"),
    ADMIN("/admin/platos"),
    OPERADOR("/operario/pedidos"),
    DOMICILIARIO(null);

    private final String loginRedirectPath;

    AuthRole(String loginRedirectPath) {
        this.loginRedirectPath = loginRedirectPath;
    }

    public Optional<String> loginRedirectPath() {
        return Optional.ofNullable(loginRedirectPath);
    }
}
