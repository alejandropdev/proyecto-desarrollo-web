/**
 * Página desafíos: solo UI. La rejilla de desafíos queda estática/vacía.
 */
(function () {
  'use strict';

  function init() {
    var container = document.getElementById('desafios-grid');
    if (container && !container.innerHTML.trim()) {
      container.innerHTML = '<p class="desafios-empty">Próximamente más desafíos.</p>';
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
