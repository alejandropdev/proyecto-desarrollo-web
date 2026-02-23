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
}

