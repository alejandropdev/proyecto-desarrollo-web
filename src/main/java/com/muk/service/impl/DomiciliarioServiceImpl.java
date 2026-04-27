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
        if (request == null) {
            return new DomiciliarioResult(null, "Datos inválidos.");
        }

        if (domiciliarioRepository.findByCelular(request.celular()).isPresent()) {
            return new DomiciliarioResult(null, "Ya existe un domiciliario con ese número de celular.");
        }

        if (domiciliarioRepository.findByCedula(request.cedula()).isPresent()) {
            return new DomiciliarioResult(null, "Ya existe un domiciliario con esa cédula.");
        }

        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setNombre(request.nombre().trim());
        domiciliario.setCelular(request.celular().trim());
        domiciliario.setCedula(request.cedula().trim());
        domiciliario.setDisponible(true);

        // Si tu entidad Domiciliario tiene activo, lo dejamos activo al crearlo.
        try {
            domiciliario.setActivo(true);
        } catch (Exception ignored) {
            // Compatibilidad si la entidad aún no tiene el campo activo.
        }

        return new DomiciliarioResult(domiciliarioRepository.save(domiciliario), null);
    }

    @Override
    public DomiciliarioResult actualizar(Long id, ApiDtos.DomiciliarioUpsertRequest request) {
        Optional<Domiciliario> opt = findById(id);

        if (opt.isEmpty()) {
            return new DomiciliarioResult(null, "Domiciliario no encontrado.");
        }

        Domiciliario domiciliario = opt.get();

        domiciliario.setNombre(request.nombre().trim());
        domiciliario.setCelular(request.celular().trim());
        domiciliario.setCedula(request.cedula().trim());

        return new DomiciliarioResult(domiciliarioRepository.save(domiciliario), null);
    }

    @Override
    public void eliminar(Long id) {
        domiciliarioRepository.findById(id).ifPresent(domiciliario -> {
            try {
                domiciliarioRepository.delete(domiciliario);
            } catch (Exception e) {
                // Si tiene pedidos asociados, no se puede borrar físicamente.
                // En ese caso se deja inactivo/no disponible.
                try {
                    domiciliario.setActivo(false);
                } catch (Exception ignored) {
                    // Compatibilidad si la entidad aún no tiene activo.
                }

                domiciliario.setDisponible(false);
                domiciliarioRepository.save(domiciliario);
            }
        });
    }

    @Override
    public void desactivar(Long id) {
        domiciliarioRepository.findById(id).ifPresent(d -> {
            try {
                d.setActivo(false);
            } catch (Exception ignored) {
                // Compatibilidad si la entidad aún no tiene activo.
            }

            d.setDisponible(false);
            domiciliarioRepository.save(d);
        });
    }

    @Override
    public void activar(Long id) {
        domiciliarioRepository.findById(id).ifPresent(d -> {
            try {
                d.setActivo(true);
            } catch (Exception ignored) {
                // Compatibilidad si la entidad aún no tiene activo.
            }

            d.setDisponible(true);
            domiciliarioRepository.save(d);
        });
    }
}