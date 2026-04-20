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
    this.clienteService.login(this.email, this.password).subscribe({
      next: (cliente) => {
        localStorage.setItem('clienteEmail', cliente.email);
        localStorage.setItem('clienteId', cliente.id.toString());
        this.router.navigate(['/clientes/perfil']);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible iniciar sesión.';
      },
    });
  }
}
