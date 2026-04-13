import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ClienteService } from '../../services/cliente.service';

@Component({
  selector: 'app-registro',
  templateUrl: './registro.component.html',
  styleUrls: ['./registro.component.css']
})
export class RegistroComponent {
  form = {
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    direccion: '',
    contrasena: ''
  };
  error = '';

  constructor(
    private readonly clienteService: ClienteService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.error = '';
    this.clienteService.registro(this.form).subscribe({
      next: (cliente) => {
        localStorage.setItem('clienteEmail', cliente.email);
        this.router.navigate(['/clientes/perfil']);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible crear la cuenta.';
      }
    });
  }
}
