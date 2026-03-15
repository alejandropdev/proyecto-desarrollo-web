package com.muk.repository;

import com.muk.entities.SeleccionAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeleccionAdicionalRepository extends JpaRepository<SeleccionAdicional, Long> {

    List<SeleccionAdicional> findByItemCarritoId(Long itemCarritoId);

    void deleteByItemCarritoId(Long itemCarritoId);
}
