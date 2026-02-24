// Entrada: monta MVC y prueba por consola.
import { foodDb } from './data/fakeDb.js';
import { FoodRepository } from './repositories/FoodRepository.js';
import { FoodService } from './services/FoodService.js';
import { FoodController } from './controllers/FoodController.js';

const repository = new FoodRepository({ db: foodDb });
const service = new FoodService({ repository });
const controller = new FoodController({ service });

function listFoods() {
  return controller.listFoods();
}

function getFoodDetail(id) {
  return controller.getFoodDetail(id);
}

function getFoodsByCategory(category) {
  return service.getFoodsByCategory(category);
}

function searchFoods(query) {
  return service.searchFoods(query);
}

console.log(listFoods());

const idReal = service.getAllFoods()[0]?.id;
console.log(getFoodDetail(idReal));

console.log(getFoodsByCategory('Burgers'));
console.log(searchFoods('burger'));

