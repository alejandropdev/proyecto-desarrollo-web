package com.muk.repository;

import com.muk.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria_NombreIgnoreCase(String nombre);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}