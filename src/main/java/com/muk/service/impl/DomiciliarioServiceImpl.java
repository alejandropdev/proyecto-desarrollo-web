package com.muk.service.impl;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Domiciliario;
import com.muk.repository.DomiciliarioRepository;
import com.muk.service.DomiciliarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    private final DomiciliarioRepository domiciliarioRepository;

    @Autowired
    public DomiciliarioServiceImpl(DomiciliarioRepository domiciliarioRepository) {
        this.domiciliarioRepository = domiciliarioRepository;
    }

    @Override
    public List<Domiciliario> findAll() {
        return domiciliarioRepository.findAll();
    }

    @Override
    public List<Domiciliario> findDisponibles() {
        return domiciliarioRepository.findByDisponibleTrue();
    }

    @Override
    public Optional<Domiciliario> findById(Long id) {
        return id == null ? Optional.empty() : domiciliarioRepository.findById(id);
    }

    @Override
    public DomiciliarioResult crear(ApiDtos.DomiciliarioUpsertRequest request) {
        // Validar unicidad de celular y cédula
        if (domiciliarioRepository.findByCelular(request.celular()).isPresent()) {
            return new DomiciliarioResult(null, "Ya existe un domiciliario con ese número de celular.");
        }
        if (domiciliarioRepository.findByCedula(request.cedula()).isPresent()) {
            return new DomiciliarioResult(null, "Ya existe un domiciliario con esa cédula.");
        }

        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setNombre(request.nombre());
        domiciliario.setCelular(request.celular());
        domiciliario.setCedula(request.cedula());
        domiciliario.setDisponible(true);

        return new DomiciliarioResult(domiciliarioRepository.save(domiciliario), null);
    }

    @Override
    public DomiciliarioResult actualizar(Long id, ApiDtos.DomiciliarioUpsertRequest request) {
        Optional<Domiciliario> opt = domiciliarioRepository.findById(id);
        if (opt.isEmpty()) {
            return new DomiciliarioResult(null, "Domiciliario no encontrado.");
        }

        Domiciliario domiciliario = opt.get();
        domiciliario.setNombre(request.nombre());
        domiciliario.setCelular(request.celular());
        domiciliario.setCedula(request.cedula());

        return new DomiciliarioResult(domiciliarioRepository.save(domiciliario), null);
    }

    @Override
    public void desactivar(Long id) {
        // Marcar como no disponible (no trabaja hoy)
        domiciliarioRepository.findById(id).ifPresent(d -> {
            d.setDisponible(false);
            domiciliarioRepository.save(d);
        });
    }

    @Override
    public void activar(Long id) {
        // Marcar como disponible (vuelve a trabajar)
        domiciliarioRepository.findById(id).ifPresent(d -> {
            d.setDisponible(true);
            domiciliarioRepository.save(d);
        });
    }
}
