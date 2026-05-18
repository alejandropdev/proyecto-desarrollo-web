package com.muk.service.impl;

import com.muk.entities.Cliente;
import com.muk.entities.Role;
import com.muk.entities.UserEntity;
import com.muk.repository.ClienteRepository;
import com.muk.repository.RoleRepository;
import com.muk.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClienteServiceImpl(ClienteRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientesResult findAll() {
        return new ClientesResult(repository.findAll());
    }

    @Override
    public ClienteResult findById(Long id) {
        if (id == null) {
            return new ClienteResult(null, "Id inválido.");
        }
        return repository.findById(id)
                .map(cliente -> new ClienteResult(cliente, null))
                .orElseGet(() -> new ClienteResult(null, "Cliente no encontrado."));
    }

    @Override
    public ClienteResult create(ClienteUpsertCommand command) {
        ValidationResult validation = validateRequiredFields(command, true);
        if (!validation.valid()) {
            return new ClienteResult(null, validation.errorMessage());
        }

        String normalizedEmail = command.email().trim();
        if (repository.findByEmail(normalizedEmail).isPresent()) {
            return new ClienteResult(null, "El email ya está registrado.");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(command.nombre().trim());
        cliente.setApellido(command.apellido().trim());
        cliente.setEmail(normalizedEmail);
        cliente.setTelefono(command.telefono().trim());
        cliente.setDireccion(command.direccion().trim());

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName("ROLE_CLIENTE").ifPresent(roles::add);
        UserEntity user = UserEntity.builder()
                .username(normalizedEmail)
                .password(passwordEncoder.encode(command.contrasena().trim()))
                .roles(roles)
                .build();
        cliente.setUserEntity(user);

        return new ClienteResult(repository.save(cliente), null);
    }

    @Override
    public ClienteResult update(Long id, ClienteUpsertCommand command) {
        if (id == null) {
            return new ClienteResult(null, "Id inválido.");
        }
        ValidationResult validation = validateRequiredFields(command, false);
        if (!validation.valid()) {
            return new ClienteResult(null, validation.errorMessage());
        }

        Optional<Cliente> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            return new ClienteResult(null, "Cliente no encontrado.");
        }

        Cliente existing = existingOpt.get();
        String normalizedEmail = command.email().trim();
        Optional<Cliente> byEmail = repository.findByEmail(normalizedEmail);
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
            return new ClienteResult(null, "El email ya está registrado.");
        }

        existing.setNombre(command.nombre().trim());
        existing.setApellido(command.apellido().trim());
        existing.setEmail(normalizedEmail);
        existing.setTelefono(command.telefono().trim());
        existing.setDireccion(command.direccion().trim());

        UserEntity user = existing.getUserEntity();
        if (user != null) {
            user.setUsername(normalizedEmail);
            if (command.contrasena() != null && !command.contrasena().isBlank()) {
                user.setPassword(passwordEncoder.encode(command.contrasena().trim()));
            }
        }

        return new ClienteResult(repository.save(existing), null);
    }

    @Override
    public ActionResult delete(Long id) {
        if (id == null) {
            return new ActionResult(false, "Id inválido.");
        }
        if (repository.findById(id).isEmpty()) {
            return new ActionResult(false, "Cliente no encontrado.");
        }
        repository.deleteById(id);
        return new ActionResult(true, null);
    }

    @Override
    public ClienteResult findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return new ClienteResult(null, "Email no registrado.");
        }
        return repository.findByEmail(email.trim())
                .map(cliente -> new ClienteResult(cliente, null))
                .orElseGet(() -> new ClienteResult(null, "Usuario no encontrado."));
    }

    @Override
    public LoginResult login(String email, String password) {
        if (email == null || email.isBlank()) {
            return new LoginResult(null, "Email no registrado.");
        }
        if (password == null || password.isBlank()) {
            return new LoginResult(null, "Credenciales inválidas.");
        }

        Optional<Cliente> byEmail = repository.findByEmail(email.trim());
        if (byEmail.isEmpty()) {
            return new LoginResult(null, "Email no registrado.");
        }

        Cliente cliente = byEmail.get();
        UserEntity user = cliente.getUserEntity();
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return new LoginResult(null, "Credenciales inválidas.");
        }

        return new LoginResult(cliente, null);
    }

    @Override
    public ClienteResult registro(ClienteUpsertCommand command) {
        return create(command);
    }

    @Override
    public RegistroResult registrarConValidacion(ClienteUpsertCommand command) {
        ClienteResult result = create(command);
        return new RegistroResult(result.cliente(), result.errorMessage());
    }

    @Override
    public PerfilResult obtenerPerfilPorEmail(String email, String missingEmailMessage) {
        if (email == null || email.isBlank()) {
            return new PerfilResult(null, missingEmailMessage);
        }
        return repository.findByEmail(email.trim())
                .map(cliente -> new PerfilResult(cliente, null))
                .orElseGet(() -> new PerfilResult(null, "Usuario no encontrado."));
    }

    @Override
    public ActionResult actualizarPerfil(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            return new ActionResult(false, "Datos inválidos.");
        }
        // contrasena null → no se actualiza la contraseña
        ClienteUpsertCommand command = new ClienteUpsertCommand(
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                null
        );
        ClienteResult result = update(cliente.getId(), command);
        return new ActionResult(result.success(), result.errorMessage());
    }

    @Override
    public ActionResult eliminarPerfilPorEmail(String email, String missingEmailMessage) {
        if (email == null || email.isBlank()) {
            return new ActionResult(false, missingEmailMessage);
        }

        Optional<Cliente> cliente = repository.findByEmail(email.trim());
        if (cliente.isEmpty()) {
            return new ActionResult(false, "Usuario no encontrado.");
        }

        Long clienteId = cliente.get().getId();
        if (clienteId == null) {
            return new ActionResult(false, "Datos inválidos.");
        }

        repository.deleteById(clienteId);
        return new ActionResult(true, null);
    }

    private ValidationResult validateRequiredFields(ClienteUpsertCommand command, boolean passwordRequired) {
        if (command == null) return new ValidationResult(false, "Datos inválidos.");
        if (isBlank(command.nombre())) return new ValidationResult(false, "El nombre es requerido.");
        if (isBlank(command.apellido())) return new ValidationResult(false, "El apellido es requerido.");
        if (isBlank(command.email())) return new ValidationResult(false, "El email es requerido.");
        if (isBlank(command.telefono())) return new ValidationResult(false, "El teléfono es requerido.");
        if (isBlank(command.direccion())) return new ValidationResult(false, "La dirección es requerida.");
        if (passwordRequired && isBlank(command.contrasena())) return new ValidationResult(false, "La contraseña es requerida.");
        return new ValidationResult(true, null);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record ValidationResult(boolean valid, String errorMessage) {
    }
}
