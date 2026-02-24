// Controlador: orquesta uso del servicio para listar y filtrar.

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
}

