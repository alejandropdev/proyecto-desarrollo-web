package com.muk.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Repositorio en memoria para categorías de productos.
 */
@Repository
public class CategoriaRepository {

    private final Set<String> categories = new LinkedHashSet<>(seedData());

    public List<String> findAll() {
        return categories.stream()
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    public void add(String category) {
        normalize(category).ifPresent(categories::add);
    }

    private static java.util.Optional<String> normalize(String category) {
        if (category == null) {
            return java.util.Optional.empty();
        }
        String normalized = category.trim();
        if (normalized.isEmpty()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(normalized);
    }

    private static List<String> seedData() {
        List<String> data = new ArrayList<>();
        data.add("Burgers");
        data.add("Ramen");
        data.add("Chicken");
        data.add("Sides");
        data.add("BBQ");
        data.add("Mex");
        data.add("Desserts");
        data.add("Drinks");
        return data;
    }
}
