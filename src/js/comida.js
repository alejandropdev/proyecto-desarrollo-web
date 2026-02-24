/**
 * Página de una sola comida: lee id de la URL y muestra su información usando el MVC.
 */
import { foodDb } from '../data/fakeDb.js';
import { FoodRepository } from '../repositories/FoodRepository.js';
import { FoodService } from '../services/FoodService.js';
import { FoodController } from '../controllers/FoodController.js';

const repository = new FoodRepository({ db: foodDb });
const service = new FoodService({ repository });
const controller = new FoodController({ service });

function formatPrice(n) {
  return '$' + Number(n).toLocaleString('es-CO') + ' COP';
}

function escapeHtml(s) {
  const div = document.createElement('div');
  div.textContent = s;
  return div.innerHTML;
}

function escapeAttr(s) {
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

function init() {
  const params = new URLSearchParams(window.location.search);
  const id = params.get('id');
  const container = document.getElementById('comida-detalle');
  const errorBlock = document.getElementById('comida-error');

  if (!id) {
    if (errorBlock) {
      errorBlock.textContent = 'No se especificó ninguna comida.';
      errorBlock.classList.remove('hidden');
    }
    if (container) container.classList.add('hidden');
    return;
  }

  const food = controller.getFoodDetail(id);
  if (!food) {
    if (errorBlock) {
      errorBlock.textContent = 'Comida no encontrada.';
      errorBlock.classList.remove('hidden');
    }
    if (container) container.classList.add('hidden');
    return;
  }

  if (errorBlock) errorBlock.classList.add('hidden');
  if (container) {
    container.classList.remove('hidden');
    container.innerHTML =
      '<div class="comida-detalle-grid">' +
      '<div class="comida-detalle-img-wrap">' +
      '<img class="comida-detalle-img" src="' +
      escapeAttr(food.imageUrl) +
      '" alt="' +
      escapeAttr(food.name) +
      '">' +
      '</div>' +
      '<div class="comida-detalle-info">' +
      '<span class="menu-card-badge">' +
      escapeHtml(food.category) +
      '</span>' +
      '<h1 class="comida-detalle-name">' +
      escapeHtml(food.name) +
      '</h1>' +
      '<p class="comida-detalle-desc">' +
      escapeHtml(food.description) +
      '</p>' +
      '<p class="comida-detalle-price">' +
      formatPrice(food.price) +
      '</p>' +
      '<p class="comida-detalle-available">' +
      (food.available ? 'Disponible' : 'No disponible') +
      '</p>' +
      '<a href="menu.html" class="btn btn-orange">Volver al menú</a>' +
      '</div>' +
      '</div>';
  }
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init);
} else {
  init();
}
