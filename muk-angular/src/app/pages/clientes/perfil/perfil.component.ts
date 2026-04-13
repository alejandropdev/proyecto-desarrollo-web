import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Cliente } from '../../../models/cliente';
import { ClienteService } from '../../../services/cliente.service';

@Component({
  selector: 'app-perfil',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent implements OnInit {
  cliente?: Cliente;
  error = '';
  editMode = false;
  form = {
    nombre: '',
    apellido: '',
    email: '',
    telefono: '',
    direccion: '',
    contrasena: ''
  };

  constructor(
    private readonly clienteService: ClienteService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    const email = localStorage.getItem('clienteEmail');
    if (!email) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadPerfil(email);
  }

  toggleEdit(): void {
    this.editMode = !this.editMode;
    if (this.cliente && this.editMode) {
      this.form = {
        nombre: this.cliente.nombre,
        apellido: this.cliente.apellido,
        email: this.cliente.email,
        telefono: this.cliente.telefono,
        direccion: this.cliente.direccion,
        contrasena: ''
      };
    }
  }

  save(): void {
    const emailOriginal = localStorage.getItem('clienteEmail');
    if (!emailOriginal) return;
    this.clienteService.updatePerfil(emailOriginal, this.form).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        localStorage.setItem('clienteEmail', cliente.email);
        this.editMode = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible actualizar el perfil.';
      }
    });
  }

  deleteAccount(): void {
    const email = localStorage.getItem('clienteEmail');
    if (!email || !window.confirm('¿Eliminar tu cuenta?')) return;
    this.clienteService.deletePerfil(email).subscribe({
      next: () => {
        localStorage.removeItem('clienteEmail');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible eliminar la cuenta.';
      }
    });
  }

  private loadPerfil(email: string): void {
    this.clienteService.getPerfil(email).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}
