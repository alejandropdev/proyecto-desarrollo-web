(function () {
  'use strict';

  // Hamburger menu
  function setupNavMenu() {
    var nav = document.getElementById('navbar');
    var toggle = document.getElementById('nav-toggle');
    var menu = document.getElementById('nav-menu');
    if (!nav || !toggle || !menu) return;

    function openMenu() {
      nav.classList.add('is-open');
      document.body.classList.add('menu-open');
      toggle.setAttribute('aria-expanded', 'true');
      toggle.setAttribute('aria-label', 'Cerrar menú');
    }

    function closeMenu() {
      nav.classList.remove('is-open');
      document.body.classList.remove('menu-open');
      toggle.setAttribute('aria-expanded', 'false');
      toggle.setAttribute('aria-label', 'Abrir menú');
    }

    function toggleMenu() {
      if (nav.classList.contains('is-open')) closeMenu();
      else openMenu();
    }

    toggle.addEventListener('click', toggleMenu);

    menu.querySelectorAll('.nav-menu-link').forEach(function (link) {
      link.addEventListener('click', closeMenu);
    });

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && nav.classList.contains('is-open')) closeMenu();
    });

    window.addEventListener('resize', function () {
      if (window.innerWidth >= 768 && nav.classList.contains('is-open')) closeMenu();
    });
  }

  // Image fallback
  function setupImageFallback() {
    document.querySelectorAll('.hero-img, .dish-img-wrap img').forEach(function (img) {
      img.addEventListener('error', function () {
        this.setAttribute('data-error', 'true');
        var wrap = this.closest('.hero-bg') || this.closest('.dish-img-wrap');
        if (wrap) wrap.classList.add('fallback');
      });
    });
  }

  // Smooth scroll
  function setupSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(function (link) {
      var href = link.getAttribute('href');
      if (href === '#' || href.length <= 1) return;
      link.addEventListener('click', function (e) {
        var target = document.querySelector(href);
        if (target) {
          e.preventDefault();
          target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    });
  }

  // Scroll-into-view animations
  function setupScrollAnimations() {
    var observer = new IntersectionObserver(
      function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) {
            entry.target.classList.add('in-view');
          }
        });
      },
      { threshold: 0.1, rootMargin: '0px 0px -40px 0px' }
    );

    ['concept', 'featured', 'cta'].forEach(function (id) {
      var section = document.querySelector('.' + id);
      if (section) observer.observe(section);
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    setupNavMenu();
    setupImageFallback();
    setupSmoothScroll();
    setupScrollAnimations();
  });
})();
