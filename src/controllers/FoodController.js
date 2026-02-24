// Controller: View → Controller → Service → Repository. Only talks to Service.

export class FoodController {
  constructor({ service }) {
    this.service = service;
  }

  listFoods() {
    return this.service.getAllFoods();
  }

  getFoodDetail(id) {
    return this.service.getFoodById(id);
  }

  getFoodsByCategory(category) {
    return this.service.getFoodsByCategory(category);
  }

  searchFoods(query) {
    return this.service.searchFoods(query);
  }
}

