package com.muk.food;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory repository with seed data matching src/data/fakeDb.js.
 */
@Repository
public class FoodRepository {

    private final Map<String, Producto> db = new ConcurrentHashMap<>();

    public FoodRepository() {
        seedData().forEach(p -> db.put(p.getId(), p));
    }

    public List<Producto> findAll() {
        return new ArrayList<>(db.values());
    }

    public Optional<Producto> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    public List<Producto> findByCategory(String category) {
        String want = (category == null ? "" : category).trim().toLowerCase();
        return db.values().stream()
                .filter(p -> (p.getCategory() != null && p.getCategory().trim().toLowerCase().equals(want)))
                .collect(Collectors.toList());
    }

    public List<Producto> searchByName(String query) {
        if (query == null || query.trim().isEmpty()) return Collections.emptyList();
        String q = query.trim().toLowerCase();
        return db.values().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    private static List<Producto> seedData() {
        return List.of(
                new Producto("FOOD-001", "The Beast Burger",
                        "Doble carne smash, cheddar, tocino ahumado, cebolla caramelizada y salsa Muk.",
                        29000,
                        "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=80",
                        "Burgers", true),
                new Producto("FOOD-002", "Nuclear Ramen",
                        "Ramen picante nivel extremo con cerdo braseado, huevo y aceite de chili.",
                        24500,
                        "https://images.unsplash.com/photo-1604908176997-125f25cc5003?auto=format&fit=crop&w=1200&q=80",
                        "Ramen", true),
                new Producto("FOOD-003", "Titan Fried Chicken",
                        "Pollo crujiente XL con glaseado spicy-honey y pepinillos.",
                        32000,
                        "https://images.unsplash.com/photo-1604909052743-94e16f8efb58?auto=format&fit=crop&w=1200&q=80",
                        "Chicken", true),
                new Producto("FOOD-004", "Muk Loaded Fries",
                        "Papas fritas con queso, chili con carne, jalapeños y crema agria.",
                        14750,
                        "https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=1200&q=80",
                        "Sides", true),
                new Producto("FOOD-005", "Giant BBQ Ribs",
                        "Costillas BBQ a fuego lento con maíz a la mantequilla y ensalada coleslaw.",
                        38900,
                        "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=80",
                        "BBQ", true),
                new Producto("FOOD-006", "Cheese Volcano Nachos",
                        "Nachos gigantes con queso derretido, guacamole, pico de gallo y frijoles.",
                        18250,
                        "https://images.unsplash.com/photo-1600891964092-4316c288032e?auto=format&fit=crop&w=1200&q=80",
                        "Mex", true),
                new Producto("FOOD-007", "Korean Hot Wings",
                        "Alitas coreanas con gochujang, sésamo tostado y cebollín.",
                        21000,
                        "https://images.unsplash.com/photo-1604908177522-040b643537fd?auto=format&fit=crop&w=1200&q=80",
                        "Chicken", false),
                new Producto("FOOD-008", "Truffle Mushroom Burger",
                        "Carne Angus, hongos salteados, mayo de trufa y rúcula.",
                        27500,
                        "https://images.unsplash.com/photo-1550317138-10000687a72b?auto=format&fit=crop&w=1200&q=80",
                        "Burgers", true),
                new Producto("FOOD-009", "Chocolate Lava Cake",
                        "Bizcocho tibio con centro de chocolate fundido y helado de vainilla.",
                        9950,
                        "https://images.unsplash.com/photo-1542826438-bd32f43d626f?auto=format&fit=crop&w=1200&q=80",
                        "Desserts", true),
                new Producto("FOOD-010", "Citrus Soda (1L)",
                        "Bebida cítrica artesanal, servida bien fría.",
                        6500,
                        "https://images.unsplash.com/photo-1510626176961-4b57d4fbad03?auto=format&fit=crop&w=1200&q=80",
                        "Drinks", true)
        );
    }
}
