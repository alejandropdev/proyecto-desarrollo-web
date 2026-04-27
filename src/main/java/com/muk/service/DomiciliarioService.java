package com.muk.service;

import com.muk.entities.Domiciliario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de domiciliarios. Define la lógica de negocio.
 * 
 * Responsabilidades:
 * - Gestionar domiciliarios (CRUD)
 * - Activar/desactivar domiciliarios
 * - Consultar disponibilidad
 */
public interface DomiciliarioService {

    /**
     * Obtiene todos los domiciliarios del sistema.
     */
    List<Domiciliario> findAll();

    /**
     * Obtiene un domiciliario por su ID.
     */
    Optional<Domiciliario> findById(Long id);

    /**
     * Obtiene un domiciliario por su número de celular.
     */
    Optional<Domiciliario> findByCelular(String celular);

    /**
     * Obtiene un domiciliario por su número de cédula.
     */
    Optional<Domiciliario> findByCedula(String cedula);

    /**
     * Obtiene todos los domiciliarios activos.
     */
    List<Domiciliario> findAllActivos();

    /**
     * Obtiene todos los domiciliarios que están activos Y disponibles.
     * Estos pueden recibir nuevas asignaciones de pedidos.
     */
    List<Domiciliario> findAllActivosDisponibles();

    /**
     * Crea un nuevo domiciliario.
     * Valida que no exista otro con el mismo celular o cédula.
     */
    Domiciliario crear(Domiciliario domiciliario);

    /**
     * Actualiza un domiciliario existente.
     */
    Domiciliario actualizar(Long id, Domiciliario datosActualizados);

    /**
     * Elimina un domiciliario del sistema.
     */
    void eliminar(Long id);

    /**
     * Activa un domiciliario (marca como activo=true).
     * Un domiciliario activo puede ser gestionado y asignado a pedidos.
     */
    Domiciliario activar(Long id);

    /**
     * Desactiva un domiciliario (marca como activo=false).
     * Un domiciliario inactivo no puede recibir nuevas asignaciones.
     */
    Domiciliario desactivar(Long id);

    /**
     * Marca un domiciliario como disponible (disponible=true).
     * Solo los domiciliarios disponibles pueden recibir nuevos pedidos.
     */
    Domiciliario marcarDisponible(Long id);

    /**
     * Marca un domiciliario como no disponible (disponible=false).
     * Usado cuando el domiciliario tiene un pedido en camino.
     */
    Domiciliario marcarNoDisponible(Long id);
}
