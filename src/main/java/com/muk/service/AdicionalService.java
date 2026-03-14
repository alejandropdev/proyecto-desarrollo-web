package com.muk.service;

import com.muk.entities.Adicional;

import java.util.List;

public interface AdicionalService {

    List<Adicional> findByCategoriaNombre(String nombre);
}
