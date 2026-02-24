// Repository: View → Controller → Service → Repository. Only talks to data (db).

function toPlainFood(food) {
  if (!food) return null;
  if (typeof food.toObject === 'function') return food.toObject();
  return {
    id: food.id,
    name: food.name,
    description: food.description,
    price: food.price,
    imageUrl: food.imageUrl,
    category: food.category,
    available: food.available,
  };
}

function normalize(value) {
  return String(value ?? '').trim().toLowerCase();
}

export class FoodRepository {
  constructor({ db }) {
    this.db = db;
  }

  findAll() {
    return Array.from(this.db.values()).map(toPlainFood);
  }

  findById(id) {
    return toPlainFood(this.db.get(id) ?? null);
  }

  findByCategory(category) {
    const wanted = normalize(category);
    return this.findAll().filter((food) => normalize(food.category) === wanted);
  }

  searchByName(query) {
    const q = normalize(query);
    return this.findAll().filter((food) => normalize(food.name).includes(q));
  }
}

