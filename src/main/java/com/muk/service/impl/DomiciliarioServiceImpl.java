package com.muk.service.impl;

import com.muk.entities.Domiciliario;
import com.muk.repository.DomiciliarioRepository;
import com.muk.service.DomiciliarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de domiciliarios.
 * Aplicar lógica de negocio y validaciones para gestión de domiciliarios.
 */
@Service
public class DomiciliarioServiceImpl implements DomiciliarioService {

    private final DomiciliarioRepository domiciliarioRepository;

    public DomiciliarioServiceImpl(DomiciliarioRepository domiciliarioRepository) {
        this.domiciliarioRepository = domiciliarioRepository;
    }

    @Override
    public List<Domiciliario> findAll() {
        return domiciliarioRepository.findAll();
    }

    @Override
    public Optional<Domiciliario> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return domiciliarioRepository.findById(id);
    }

    @Override
    public Optional<Domiciliario> findByCelular(String celular) {
        if (celular == null || celular.isBlank()) {
            return Optional.empty();
        }
        return domiciliarioRepository.findByCelular(celular);
    }

    @Override
    public Optional<Domiciliario> findByCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            return Optional.empty();
        }
        return domiciliarioRepository.findByCedula(cedula);
    }

    @Override
    public List<Domiciliario> findAllActivos() {
        return domiciliarioRepository.findByActivoTrue();
    }

    @Override
    public List<Domiciliario> findAllActivosDisponibles() {
        return domiciliarioRepository.findByActivoTrueAndDisponibleTrue();
    }

    @Override
    @Transactional
    public Domiciliario crear(Domiciliario domiciliario) {
        // Validaciones básicas
        if (domiciliario == null) {
            throw new IllegalArgumentException("El domiciliario no puede ser nulo.");
        }

        if (domiciliario.getNombre() == null || domiciliario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del domiciliario es requerido.");
        }

        if (domiciliario.getCelular() == null || domiciliario.getCelular().isBlank()) {
            throw new IllegalArgumentException("El celular del domiciliario es requerido.");
        }

        if (domiciliario.getCedula() == null || domiciliario.getCedula().isBlank()) {
            throw new IllegalArgumentException("La cédula del domiciliario es requerida.");
        }

        // Validar que no exista otro domiciliario con el mismo celular
        if (findByCelular(domiciliario.getCelular()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un domiciliario con ese celular.");
        }

        // Validar que no exista otro domiciliario con la misma cédula
        if (findByCedula(domiciliario.getCedula()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un domiciliario con esa cédula.");
        }

        // Por defecto, nuevo domiciliario está activo y disponible
        domiciliario.setActivo(true);
        domiciliario.setDisponible(true);

        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    @Transactional
    public Domiciliario actualizar(Long id, Domiciliario datosActualizados) {
        Optional<Domiciliario> existente = findById(id);
        
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Domiciliario con ID " + id + " no encontrado.");
        }

        Domiciliario domiciliario = existente.get();

        // Actualizar campos
        if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().isBlank()) {
            domiciliario.setNombre(datosActualizados.getNombre());
        }

        if (datosActualizados.getCelular() != null && !datosActualizados.getCelular().isBlank()) {
            // Validar que el celular no esté en uso por otro domiciliario
            Optional<Domiciliario> otroConCelular = findByCelular(datosActualizados.getCelular());
            if (otroConCelular.isPresent() && !otroConCelular.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro domiciliario con ese celular.");
            }
            domiciliario.setCelular(datosActualizados.getCelular());
        }

        if (datosActualizados.getCedula() != null && !datosActualizados.getCedula().isBlank()) {
            // Validar que la cédula no esté en uso por otro domiciliario
            Optional<Domiciliario> otroConCedula = findByCedula(datosActualizados.getCedula());
            if (otroConCedula.isPresent() && !otroConCedula.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro domiciliario con esa cédula.");
            }
            domiciliario.setCedula(datosActualizados.getCedula());
        }

        return domiciliarioRepository.save(domiciliario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de domiciliario inválido.");
        }

        Optional<Domiciliario> optDomiciliario = findById(id);
        if (optDomiciliario.isEmpty()) {
            throw new IllegalArgumentException("Domiciliario con ID " + id + " no encontrado.");
        }

        Domiciliario domiciliario = optDomiciliario.get();

        try {
            // Intentar eliminar físicamente
            domiciliarioRepository.deleteById(id);
        } catch (Exception e) {
            // Si hay error de llave foránea, hacer soft delete (desactivar)
            if (e.getMessage() != null && e.getMessage().contains("foreign key")) {
                domiciliario.setActivo(false);
                domiciliario.setDisponible(false);
                domiciliarioRepository.save(domiciliario);
                // Relanzar excepción que indique que se hizo soft delete
                throw new IllegalArgumentException("Domiciliario tiene pedidos asociados. Ha sido desactivado en su lugar.");
            } else {
                throw e;
            }
        }
    }

    @Override
    @Transactional
    public Domiciliario activar(Long id) {
        Optional<Domiciliario> domiciliario = findById(id);
        
        if (domiciliario.isEmpty()) {
            throw new IllegalArgumentException("Domiciliario con ID " + id + " no encontrado.");
        }

        Domiciliario d = domiciliario.get();
        d.setActivo(true);
        return domiciliarioRepository.save(d);
    }

    @Override
    @Transactional
    public Domiciliario desactivar(Long id) {
        Optional<Domiciliario> domiciliario = findById(id);
        
        if (domiciliario.isEmpty()) {
            throw new IllegalArgumentException("Domiciliario con ID " + id + " no encontrado.");
        }

        Domiciliario d = domiciliario.get();
        d.setActivo(false);
        // Cuando se desactiva, también se marca como no disponible
        d.setDisponible(false);
        return domiciliarioRepository.save(d);
    }

    @Override
    @Transactional
    public Domiciliario marcarDisponible(Long id) {
        Optional<Domiciliario> domiciliario = findById(id);
        
        if (domiciliario.isEmpty()) {
            throw new IllegalArgumentException("Domiciliario con ID " + id + " no encontrado.");
        }

        Domiciliario d = domiciliario.get();
        d.setDisponible(true);
        return domiciliarioRepository.save(d);
    }

    @Override
    @Transactional
    public Domiciliario marcarNoDisponible(Long id) {
        Optional<Domiciliario> domiciliario = findById(id);
        
        if (domiciliario.isEmpty()) {
            throw new IllegalArgumentException("Domiciliario con ID " + id + " no encontrado.");
        }

        Domiciliario d = domiciliario.get();
        d.setDisponible(false);
        return domiciliarioRepository.save(d);
    }
}
