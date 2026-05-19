import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ClienteService } from '../../services/cliente.service';
import { AuthService, AuthSession } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';

  constructor(
    private readonly authService: AuthService,
    private readonly clienteService: ClienteService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.error = '';
    this.authService.login(this.email, this.password).subscribe({
      next: (session) => {
        this.handleLoginSuccess(session);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible iniciar sesión.';
      },
    });
  }

  private handleLoginSuccess(session: AuthSession): void {
    if (session.role !== 'ROLE_CLIENTE') {
      this.router.navigate([session.redirectPath || this.authService.redirectPathFor(session.role)]);
      return;
    }

    this.clienteService.getPerfil(this.email).subscribe({
      next: (cliente) => {
        localStorage.setItem('clienteEmail', cliente.email);
        localStorage.setItem('clienteId', cliente.id.toString());
        this.router.navigate([session.redirectPath || '/clientes/perfil']);
      },
      error: () => {
        this.error = 'Error obteniendo el perfil';
      }
    });
  }
}
