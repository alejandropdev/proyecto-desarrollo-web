package com.muk.repository;

import com.muk.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findAllByOrderByNombreAsc();
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
