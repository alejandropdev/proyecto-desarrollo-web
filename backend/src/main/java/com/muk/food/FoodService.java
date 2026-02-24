package com.muk.food;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    private final FoodRepository repository;

    public FoodService(FoodRepository repository) {
        this.repository = repository;
    }

    public List<Producto> getAllFoods() {
        return repository.findAll();
    }

    public Optional<Producto> getFoodById(String id) {
        if (id == null || id.trim().isEmpty()) return Optional.empty();
        return repository.findById(id.trim());
    }

    public List<Producto> getFoodsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) return List.of();
        return repository.findByCategory(category.trim());
    }

    public List<Producto> searchFoods(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();
        return repository.searchByName(query.trim());
    }
}
