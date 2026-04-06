package com.muk.service.impl;

import com.muk.entities.Operador;
import com.muk.repository.OperadorRepository;
import com.muk.service.OperadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository repository;

    @Override
    public List<Operador> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Operador> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Override
    public Operador save(Operador operador) {
        if (operador == null) return null;
        return repository.save(operador);
    }

    @Override
    public void delete(Long id) {
        if (id != null) {
            findById(id).ifPresent(operador -> {
                operador.setActivo(false);
                repository.save(operador);
            });
        }
    }

    @Override
    public Optional<Operador> findByUsuario(String usuario) {
        return repository.findByUsuario(usuario);
    }

    @Override
    public Optional<Operador> findByUsuarioAndContrasena(String usuario, String contrasena) {
        return repository.findByUsuarioAndContrasenaHash(usuario, contrasena);
    }
}
