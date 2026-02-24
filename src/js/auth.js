/**
 * Páginas login y registro: solo UI. Sin validación ni envío.
 */
(function () {
  'use strict';

  function init() {
    var loginForm = document.getElementById('auth-login-form');
    var registerForm = document.getElementById('auth-register-form');
    if (loginForm) {
      loginForm.addEventListener('submit', function (e) { e.preventDefault(); });
    }
    if (registerForm) {
      registerForm.addEventListener('submit', function (e) { e.preventDefault(); });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
