/**
 * Página ubicación: solo UI. Botón reservar sin lógica de sesión.
 */
(function () {
  'use strict';

  function init() {
    var btn = document.querySelector('.ubicacion-info .btn-orange');
    if (btn) {
      btn.addEventListener('click', function (e) {
        e.preventDefault();
      });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
