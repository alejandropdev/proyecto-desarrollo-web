package com.muk.repository;

import com.muk.entities.Domiciliario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Domiciliario.
 * Proporciona operaciones CRUD y queries específicas.
 */
@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long> {

    /**
     * Busca un domiciliario por su número de celular.
     */
    Optional<Domiciliario> findByCelular(String celular);

    /**
     * Busca un domiciliario por su número de cédula.
     */
    Optional<Domiciliario> findByCedula(String cedula);

    /**
     * Obtiene todos los domiciliarios disponibles (están disponibles para recibir pedidos).
     */
    List<Domiciliario> findByDisponibleTrue();

    /**
     * Obtiene todos los domiciliarios activos.
     * Un domiciliario activo es aquel que puede ser gestionado por el admin.
     */
    List<Domiciliario> findByActivoTrue();

    /**
     * Obtiene todos los domiciliarios inactivos.
     */
    List<Domiciliario> findByActivoFalse();

    /**
     * Obtiene domiciliarios que están activos Y disponibles.
     * Estos pueden recibir nuevos pedidos.
     */
    List<Domiciliario> findByActivoTrueAndDisponibleTrue();
}
