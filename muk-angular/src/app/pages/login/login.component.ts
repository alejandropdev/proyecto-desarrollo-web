import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ClienteService } from '../../services/cliente.service';

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
    private readonly clienteService: ClienteService,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.error = '';
    // Call our new auth service endpoint implicitly via http or create a method in clienteService
    this.clienteService.login(this.email, this.password).subscribe({
      next: (res: any) => {
        // If we change ClienteService login to call /api/auth/login it returns a token
        const token = res.token;
        if (token) {
          sessionStorage.setItem('AuthToken', token);
          // Now fetch profile
          this.clienteService.getPerfil(this.email).subscribe({
            next: (cliente) => {
              localStorage.setItem('clienteEmail', cliente.email);
              localStorage.setItem('clienteId', cliente.id.toString());
              this.router.navigate(['/clientes/perfil']);
            },
            error: () => {
               this.error = 'Error obteniendo el perfil';
            }
          });
        }
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible iniciar sesión.';
      },
    });
  }
}
