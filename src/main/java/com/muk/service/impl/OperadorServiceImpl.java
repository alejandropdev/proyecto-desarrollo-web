package com.muk.service.impl;

import com.muk.entities.Operador;
import com.muk.entities.Role;
import com.muk.entities.UserEntity;
import com.muk.repository.OperadorRepository;
import com.muk.repository.RoleRepository;
import com.muk.service.OperadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OperadorServiceImpl(OperadorRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OperadoresResult findAllActive() {
        List<Operador> active = repository.findAll().stream()
                .filter(operador -> Boolean.TRUE.equals(operador.getActivo()))
                .toList();
        return new OperadoresResult(active);
    }

    @Override
    public OperadorResult findActiveById(Long id) {
        if (id == null) {
            return new OperadorResult(null, "Id inválido.");
        }
        return repository.findById(id)
                .filter(operador -> Boolean.TRUE.equals(operador.getActivo()))
                .map(operador -> new OperadorResult(operador, null))
                .orElseGet(() -> new OperadorResult(null, "Operador no encontrado."));
    }

    @Override
    public OperadorResult create(OperadorUpsertCommand command) {
        ValidationResult validation = validateCommand(command, true);
        if (!validation.valid()) {
            return new OperadorResult(null, validation.errorMessage());
        }
        String usuario = command.usuario().trim();
        if (repository.findByUsuario(usuario).isPresent()) {
            return new OperadorResult(null, "El usuario ya está registrado.");
        }

        Set<Role> roles = new HashSet<>();
        roleRepository.findByName("ROLE_OPERADOR").ifPresent(roles::add);
        UserEntity user = UserEntity.builder()
                .username(usuario)
                .password(passwordEncoder.encode(command.contrasena().trim()))
                .roles(roles)
                .build();

        Operador operador = new Operador();
        operador.setNombre(command.nombre().trim());
        operador.setUsuario(usuario);
        operador.setActivo(true);
        operador.setUserEntity(user);

        return new OperadorResult(repository.save(operador), null);
    }

    @Override
    public OperadorResult update(Long id, OperadorUpsertCommand command) {
        if (id == null) {
            return new OperadorResult(null, "Id inválido.");
        }
        ValidationResult validation = validateCommand(command, false);
        if (!validation.valid()) {
            return new OperadorResult(null, validation.errorMessage());
        }
        Optional<Operador> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty() || !Boolean.TRUE.equals(existingOpt.get().getActivo())) {
            return new OperadorResult(null, "Operador no encontrado.");
        }
        Operador existing = existingOpt.get();
        String usuario = command.usuario().trim();
        Optional<Operador> byUsuario = repository.findByUsuario(usuario);
        if (byUsuario.isPresent() && !byUsuario.get().getId().equals(id)) {
            return new OperadorResult(null, "El usuario ya está registrado.");
        }

        existing.setNombre(command.nombre().trim());
        existing.setUsuario(usuario);

        UserEntity user = existing.getUserEntity();
        if (user != null) {
            user.setUsername(usuario);
            if (command.contrasena() != null && !command.contrasena().isBlank()) {
                user.setPassword(passwordEncoder.encode(command.contrasena().trim()));
            }
        }

        return new OperadorResult(repository.save(existing), null);
    }

    @Override
    public ActionResult delete(Long id) {
        if (id == null) {
            return new ActionResult(false, "Id inválido.");
        }
        Optional<Operador> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty() || !Boolean.TRUE.equals(existingOpt.get().getActivo())) {
            return new ActionResult(false, "Operador no encontrado.");
        }
        Operador operador = existingOpt.get();
        operador.setActivo(false);
        repository.save(operador);
        return new ActionResult(true, null);
    }

    @Override
    public LoginResult login(String usuario, String password) {
        if (usuario == null || usuario.isBlank() || password == null || password.isBlank()) {
            return new LoginResult(null, "Usuario o contraseña incorrectos");
        }
        Optional<Operador> operadorOpt = repository.findByUsuario(usuario.trim());
        if (operadorOpt.isEmpty() || !Boolean.TRUE.equals(operadorOpt.get().getActivo())) {
            return new LoginResult(null, "Usuario o contraseña incorrectos");
        }
        Operador operador = operadorOpt.get();
        UserEntity user = operador.getUserEntity();
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return new LoginResult(null, "Usuario o contraseña incorrectos");
        }
        return new LoginResult(operador, null);
    }

    private ValidationResult validateCommand(OperadorUpsertCommand command, boolean passwordRequired) {
        if (command == null) return new ValidationResult(false, "Datos inválidos.");
        if (command.nombre() == null || command.nombre().isBlank()) return new ValidationResult(false, "El nombre es requerido.");
        if (command.usuario() == null || command.usuario().isBlank()) return new ValidationResult(false, "El usuario es requerido.");
        if (passwordRequired && (command.contrasena() == null || command.contrasena().isBlank())) {
            return new ValidationResult(false, "La contraseña es requerida.");
        }
        return new ValidationResult(true, null);
    }

    private record ValidationResult(boolean valid, String errorMessage) {
    }
}
