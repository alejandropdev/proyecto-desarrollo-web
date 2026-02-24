package com.muk.food;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FoodController {

    private static final Logger log = LoggerFactory.getLogger(FoodController.class);

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<Producto> listFoods() {
        log.info("GET /api/foods -> listFoods()");
        return foodService.getAllFoods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getFoodDetail(@PathVariable String id) {
        log.info("GET /api/foods/{} -> getFoodDetail({})", id, id);
        return foodService.getFoodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "category")
    public List<Producto> getFoodsByCategory(@RequestParam String category) {
        log.info("GET /api/foods?category={} -> getFoodsByCategory({})", category, category);
        return foodService.getFoodsByCategory(category);
    }

    @GetMapping(params = "q")
    public List<Producto> searchFoods(@RequestParam String q) {
        log.info("GET /api/foods?q={} -> searchFoods({})", q, q);
        return foodService.searchFoods(q);
    }
}
