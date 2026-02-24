/**
 * View: Página menú. Flow View → Controller → Service → Repository; solo usa foodController.
 */
import { foodController } from '../bootstrap.js';

// Convierte producto del modelo a formato de vista
function toItem(food) {
  if (!food) return null;
  return {
    id: food.id,
    nombre: food.name,
    descripcion: food.description,
    precio: food.price,
    imagen: food.imageUrl,
    categoria: food.category
  };
}

// Formato de precio y escape para HTML
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

// Estado de vista y filtro
let state = {
  items: [],
  view: 'tarjetas',
  categoria: 'TODOS'
};

function getFilteredItems() {
  if (state.categoria === 'TODOS') return state.items;
  return state.items.filter((item) => item.categoria === state.categoria);
}

// Pinta las píldoras de categoría
function renderPills() {
  const container = document.getElementById('filter-pills');
  if (!container) return;
  const categorias = ['TODOS', ...new Set(state.items.map((i) => i.categoria).filter(Boolean))];
  container.innerHTML = categorias
    .map(
      (cat) =>
        '<button type="button" data-categoria="' +
        escapeAttr(cat) +
        '" class="' +
        (cat === state.categoria ? 'active' : '') +
        '">' +
        escapeHtml(cat) +
        '</button>'
    )
    .join('');
  bindFilters();
}

// Pinta la rejilla de tarjetas
function renderCards() {
  const container = document.getElementById('menu-cards');
  if (!container) return;
  const items = getFilteredItems();
  container.innerHTML = items
    .map(
      (item) =>
        '<article class="menu-card">' +
        '<img class="menu-card-img" src="' +
        escapeAttr(item.imagen) +
        '" alt="' +
        escapeAttr(item.nombre) +
        '">' +
        '<div class="menu-card-body">' +
        '<span class="menu-card-badge">' +
        escapeHtml(item.categoria) +
        '</span>' +
        '<h3 class="menu-card-name">' +
        escapeHtml(item.nombre) +
        '</h3>' +
        '<p class="menu-card-desc">' +
        escapeHtml(item.descripcion) +
        '</p>' +
        '<div class="menu-card-divider"></div>' +
        '<div class="menu-card-footer">' +
        '<span class="menu-card-price">' +
        formatPrice(item.precio) +
        '</span>' +
        '<a href="comida.html?id=' +
        escapeAttr(item.id) +
        '" class="btn btn-orange">VER DETALLE</a>' +
        '</div>' +
        '</div>' +
        '</article>'
    )
    .join('');
}

// Pinta la tabla
function renderTable() {
  const tbody = document.getElementById('menu-tabla-body');
  if (!tbody) return;
  const items = getFilteredItems();
  tbody.innerHTML = items
    .map(
      (item) =>
        '<tr>' +
        '<td><img class="menu-tabla-img" src="' +
        escapeAttr(item.imagen) +
        '" alt=""></td>' +
        '<td><span class="menu-tabla-name">' +
        escapeHtml(item.nombre) +
        '</span><span class="menu-tabla-desc">' +
        escapeHtml(item.descripcion) +
        '</span></td>' +
        '<td><span class="menu-card-badge">' +
        escapeHtml(item.categoria) +
        '</span></td>' +
        '<td class="menu-tabla-price">' +
        formatPrice(item.precio) +
        '</td>' +
        '<td><a href="comida.html?id=' +
        escapeAttr(item.id) +
        '" class="btn btn-orange">VER DETALLE</a></td>' +
        '</tr>'
    )
    .join('');
}

function bindToggle() {
  document.querySelectorAll('.view-toggle button').forEach((btn) => {
    btn.addEventListener('click', function () {
      const view = this.getAttribute('data-view');
      if (!view) return;
      state.view = view;
      document.querySelectorAll('.view-toggle button').forEach((b) => b.classList.remove('active'));
      this.classList.add('active');
      document.getElementById('menu-cards-section').classList.toggle('hidden', view !== 'tarjetas');
      document.getElementById('menu-tabla-section').classList.toggle('hidden', view !== 'tabla');
    });
  });
}

function bindFilters() {
  document.querySelectorAll('.filter-pills button').forEach((btn) => {
    btn.addEventListener('click', function () {
      const cat = this.getAttribute('data-categoria');
      if (cat === undefined) return;
      state.categoria = cat;
      document.querySelectorAll('.filter-pills button').forEach((b) => b.classList.remove('active'));
      this.classList.add('active');
      renderCards();
      renderTable();
    });
  });
}

function render() {
  renderCards();
  renderTable();
}

// Inicializa: obtiene datos del controlador y pinta
function init() {
  const list = foodController.listFoods();
  state.items = list.map(toItem).filter(Boolean);
  renderPills();
  render();
  bindToggle();
  document.getElementById('menu-cards-section').classList.toggle('hidden', state.view !== 'tarjetas');
  document.getElementById('menu-tabla-section').classList.toggle('hidden', state.view !== 'tabla');
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init);
} else {
  init();
}
