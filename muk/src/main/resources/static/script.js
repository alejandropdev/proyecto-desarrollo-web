(function () {
  function wireMenu(toggleId, menuId) {
    var toggle = document.getElementById(toggleId);
    var menu = document.getElementById(menuId);
    if (!toggle || !menu) return;

    toggle.addEventListener('click', function () {
      var willOpen = menu.classList.contains('hidden');
      menu.classList.toggle('hidden', !willOpen);
      toggle.setAttribute('aria-expanded', String(willOpen));
    });

    // Cierra el menú móvil al hacer click en un link
    menu.addEventListener('click', function (e) {
      var target = e.target;
      if (target && target.tagName && target.tagName.toLowerCase() === 'a') {
        menu.classList.add('hidden');
        toggle.setAttribute('aria-expanded', 'false');
      }
    });
  }

  wireMenu('nav-toggle', 'nav-menu');
  wireMenu('nav-toggle-admin', 'nav-menu-admin');
})();
