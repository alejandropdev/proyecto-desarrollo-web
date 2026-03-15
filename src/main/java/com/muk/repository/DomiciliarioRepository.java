package com.muk.repository;

import com.muk.entities.Domiciliario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomiciliarioRepository extends JpaRepository<Domiciliario, Long> {

    Optional<Domiciliario> findByCelular(String celular);

    Optional<Domiciliario> findByCedula(String cedula);

    List<Domiciliario> findByDisponibleTrue();
}
