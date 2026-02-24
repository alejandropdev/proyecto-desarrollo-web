// View entry: uses only the controller. Flow is View → Controller → Service → Repository.
import { foodController } from './bootstrap.js';

function listFoods() {
  return foodController.listFoods();
}

function getFoodDetail(id) {
  return foodController.getFoodDetail(id);
}

function getFoodsByCategory(category) {
  return foodController.getFoodsByCategory(category);
}

function searchFoods(query) {
  return foodController.searchFoods(query);
}

console.log(listFoods());

const idReal = foodController.listFoods()[0]?.id;
console.log(getFoodDetail(idReal));

console.log(getFoodsByCategory('Burgers'));
console.log(searchFoods('burger'));

