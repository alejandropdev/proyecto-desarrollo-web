import { foodDb } from './data/fakeDb.js';
import { FoodRepository } from './repositories/FoodRepository.js';
import { FoodService } from './services/FoodService.js';
import { FoodController } from './controllers/FoodController.js';

const repository = new FoodRepository({ db: foodDb });
const service = new FoodService({ repository });
const controller = new FoodController({ service });

const foods = controller.listFoods();
console.log(foods);

const idReal = foods[0]?.id;
console.log(controller.getFoodDetail(idReal));

console.log(service.getFoodsByCategory('Burgers'));
console.log(service.searchFoods('burger'));

