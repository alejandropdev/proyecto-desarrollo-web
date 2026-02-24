// Servicio: validación y delegación al repositorio.

function requireNonEmptyString(value, fieldName) {
  if (typeof value !== 'string' || value.trim().length === 0) {
    throw new Error(`${fieldName} must be a non-empty string`);
  }
  return value.trim();
}

export class FoodService {
  constructor({ repository }) {
    this.repository = repository;
  }

  getAllFoods() {
    return this.repository.findAll();
  }

  getFoodById(id) {
    const safeId = requireNonEmptyString(id, 'id');
    return this.repository.findById(safeId);
  }

  getFoodsByCategory(category) {
    const safeCategory = requireNonEmptyString(category, 'category');
    return this.repository.findByCategory(safeCategory);
  }

  searchFoods(query) {
    if (typeof query !== 'string' || query.trim().length === 0) return [];
    return this.repository.searchByName(query);
  }
}

