package com.muk.service;

import com.muk.entities.Operador;

import java.util.List;

/**
 * Interfaz del servicio de operadores. Lógica de negocio.
 */
public interface OperadorService {

    record OperadorUpsertCommand(String nombre, String usuario, String contrasena) {
    }

    record OperadorResult(Operador operador, String errorMessage) {
        public boolean success() {
            return operador != null;
        }
    }

    record OperadoresResult(List<Operador> operadores) {
    }

    record LoginResult(Operador operador, String errorMessage) {
        public boolean success() {
            return operador != null;
        }
    }

    record ActionResult(boolean success, String errorMessage) {
    }

    OperadoresResult findAllActive();

    OperadorResult findActiveById(Long id);

    OperadorResult create(OperadorUpsertCommand command);

    OperadorResult update(Long id, OperadorUpsertCommand command);

    ActionResult delete(Long id);

    LoginResult login(String usuario, String password);
}
