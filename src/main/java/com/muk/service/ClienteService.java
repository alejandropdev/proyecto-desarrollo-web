package com.muk.service;

import com.muk.entities.Cliente;

import java.util.List;

/**
 * Interfaz del servicio de clientes.
 */
public interface ClienteService {

    record ClienteUpsertCommand(String nombre, String apellido, String email, String telefono, String direccion,
                                String contrasena) {
    }

    record ClienteResult(Cliente cliente, String errorMessage) {
        public boolean success() {
            return cliente != null;
        }
    }

    record ClientesResult(List<Cliente> clientes) {
    }

    record LoginResult(Cliente cliente, String errorMessage) {
        public boolean success() {
            return cliente != null;
        }
    }

    record RegistroResult(Cliente cliente, String errorMessage) {
        public boolean success() {
            return cliente != null;
        }
    }

    record PerfilResult(Cliente cliente, String errorMessage) {
        public boolean success() {
            return cliente != null;
        }
    }

    record ActionResult(boolean success, String errorMessage) {
    }

    ClientesResult findAll();

    ClienteResult findById(Long id);

    ClienteResult create(ClienteUpsertCommand command);

    ClienteResult update(Long id, ClienteUpsertCommand command);

    ActionResult delete(Long id);

    /**
     * Busca un cliente por email para login.
     */
    ClienteResult findByEmail(String email);

    /**
     * Valida login en capa de servicio (credenciales en texto plano).
     */
    LoginResult login(String email, String password);

    /**
     * Registra un nuevo cliente.
     */
    ClienteResult registro(ClienteUpsertCommand command);

    /**
     * Registra cliente con validaciones de negocio.
     */
    RegistroResult registrarConValidacion(ClienteUpsertCommand command);

    /**
     * Obtiene perfil por email con validaciones de acceso.
     */
    PerfilResult obtenerPerfilPorEmail(String email, String missingEmailMessage);

    /**
     * Actualiza perfil con validaciones de negocio.
     */
    ActionResult actualizarPerfil(Cliente cliente);

    /**
     * Elimina perfil por email con validaciones de acceso.
     */
    ActionResult eliminarPerfilPorEmail(String email, String missingEmailMessage);
}
