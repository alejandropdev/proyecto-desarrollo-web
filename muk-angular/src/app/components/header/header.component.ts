import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  isAuthenticated: boolean = false;
  clienteEmail: string = '';
  isMobileMenuOpen: boolean = false;
  private destroy$ = new Subject<void>();

  constructor(private router: Router) {}

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
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  logout(): void {
    localStorage.removeItem('clienteEmail');
    localStorage.removeItem('clienteId');
    this.isAuthenticated = false;
    this.clienteEmail = '';
    this.closeMobileMenu();
    this.router.navigate(['/']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
