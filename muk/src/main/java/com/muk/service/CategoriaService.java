package com.muk.service;

import com.muk.entities.Categoria;
import java.util.List;

public interface CategoriaService {

    List<Categoria> findAll();

    void addIfMissing(String category);
}