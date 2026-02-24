/**
 * Composition root: wires layers in order Repository → Service → Controller.
 * Flow is View → Controller → Service → Repository. View must only use foodController.
 */
import { foodDb } from './data/fakeDb.js';
import { FoodRepository } from './repositories/FoodRepository.js';
import { FoodService } from './services/FoodService.js';
import { FoodController } from './controllers/FoodController.js';

const repository = new FoodRepository({ db: foodDb });
const service = new FoodService({ repository });
export const foodController = new FoodController({ service });
