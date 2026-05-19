import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';
import { CarritoService } from '../../services/carrito.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated: boolean = false;
  clienteEmail: string = '';
  isMobileMenuOpen: boolean = false;
  cantidadCarrito: number = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private readonly router: Router,
    private readonly carritoService: CarritoService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.checkAuthentication();
    // Verificar autenticación en cada navegación
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe(() => {
        this.checkAuthentication();
        this.closeMobileMenu();
      });
  }

  checkAuthentication(): void {
    this.clienteEmail = localStorage.getItem('clienteEmail') || '';
    this.isAuthenticated = !!this.clienteEmail;
    this.cantidadCarrito = this.carritoService
      .cargar()
      .reduce((total, linea) => total + (linea.cantidad || 0), 0);
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.clearClientSession(),
      error: () => this.clearClientSession(),
    });
  }

  private clearClientSession(): void {
    localStorage.removeItem('clienteEmail');
    localStorage.removeItem('clienteId');
    this.carritoService.limpiar();
    this.isAuthenticated = false;
    this.clienteEmail = '';
    this.cantidadCarrito = 0;
    this.closeMobileMenu();
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
