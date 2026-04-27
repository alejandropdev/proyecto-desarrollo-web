package com.muk.service;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Domiciliario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de domiciliarios. Gestiona disponibilidad y CRUD.
 */
public interface DomiciliarioService {

    record DomiciliarioResult(Domiciliario domiciliario, String errorMessage) {
        public boolean success() {
            return domiciliario != null;
        }
    }

    /** Obtiene todos los domiciliarios. */
    List<Domiciliario> findAll();

    /** Obtiene solo los domiciliarios disponibles (para asignar a un envío). */
    List<Domiciliario> findDisponibles();

    /** Obtiene un domiciliario por su id. */
    Optional<Domiciliario> findById(Long id);

    /** Registra un nuevo domiciliario. */
    DomiciliarioResult crear(ApiDtos.DomiciliarioUpsertRequest request);

    /** Actualiza los datos de un domiciliario existente. */
    DomiciliarioResult actualizar(Long id, ApiDtos.DomiciliarioUpsertRequest request);

    /** 🔥 ELIMINAR (o desactivar) */
public String eliminar(Long id);

    /** Desactiva (no disponible) */
    void desactivar(Long id);

    /** Activa (disponible) */
    void activar(Long id);
}