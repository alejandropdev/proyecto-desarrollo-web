(function () {
  var toggle = document.getElementById('nav-toggle');
  var navbar = document.getElementById('navbar');
  if (toggle && navbar) {
    toggle.addEventListener('click', function () {
      var inner = navbar.querySelector('.navbar-inner');
      if (inner) inner.classList.toggle('expanded');
      toggle.setAttribute('aria-expanded', inner && inner.classList.contains('expanded'));
    });
  }
})();
