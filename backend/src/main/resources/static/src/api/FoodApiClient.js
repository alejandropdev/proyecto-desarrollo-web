/**
 * Client for the Spring Boot /api/foods backend.
 * Same interface as the previous FoodController: listFoods, getFoodDetail, getFoodsByCategory, searchFoods.
 * All methods return Promises (use async/await in the view).
 */
const API_BASE = '/api/foods';

async function request(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  if (!res.ok) {
    if (res.status === 404) return null;
    throw new Error(`API error: ${res.status}`);
  }
  return res.json();
}

export const foodController = {
  async listFoods() {
    const list = await request(API_BASE);
    return Array.isArray(list) ? list : [];
  },

  async getFoodDetail(id) {
    if (!id || String(id).trim() === '') return null;
    return request(`${API_BASE}/${encodeURIComponent(id.trim())}`);
  },

  async getFoodsByCategory(category) {
    if (!category || String(category).trim() === '') return [];
    const list = await request(`${API_BASE}?category=${encodeURIComponent(category.trim())}`);
    return Array.isArray(list) ? list : [];
  },

  async searchFoods(query) {
    if (!query || String(query).trim() === '') return [];
    const list = await request(`${API_BASE}?q=${encodeURIComponent(query.trim())}`);
    return Array.isArray(list) ? list : [];
  },
};
