import { Component, OnInit } from '@angular/core';
import { DomiciliarioService } from '../../services/domiciliario.service';
import { Domiciliario, DomiciliarioUpsertRequest } from '../../models/domiciliario';

/**
 * Componente para gestión de domiciliarios (Admin).
 * 
 * Responsabilidades:
 * - Listar todos los domiciliarios
 * - Crear nuevo domiciliario
 * - Actualizar domiciliario existente
 * - Eliminar domiciliario
 * - Activar/desactivar domiciliario
 */
@Component({
  selector: 'app-domiciliarios',
  templateUrl: './domiciliarios.component.html',
  styleUrls: ['./domiciliarios.component.css'],
})
export class DomiciliariosComponent implements OnInit {
  // Lista de domiciliarios
  domiciliarios: Domiciliario[] = [];

  // Estados
  cargando: boolean = false;
  mensajeError: string = '';
  mensajeExito: string = '';

  // Controles del modal de formulario
  mostrarFormulario: boolean = false;
  editandoDomiciliario: Domiciliario | null = null;
  formularioEnvio: boolean = false;

  constructor(private domiciliarioService: DomiciliarioService) {}

  ngOnInit(): void {
    console.log('✅ DomiciliariosComponent cargado correctamente');
    this.cargarDomiciliarios();
  }

  /**
   * Carga la lista de domiciliarios desde la API
   */
  cargarDomiciliarios(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.domiciliarioService.listarTodos().subscribe({
      next: (domiciliarios: Domiciliario[]) => {
        this.domiciliarios = domiciliarios;
        this.cargando = false;
        console.log('Domiciliarios cargados:', domiciliarios);
      },
      error: (error) => {
        this.mensajeError = 'Error al cargar domiciliarios. Intenta nuevamente.';
        this.cargando = false;
        console.error('Error:', error);
      },
    });
  }

  /**
   * Abre el formulario para crear nuevo domiciliario
   */
  abrirFormularioNuevo(): void {
    this.editandoDomiciliario = null;
    this.mostrarFormulario = true;
  }

  /**
   * Abre el formulario para editar un domiciliario existente
   */
  abrirFormularioEditar(domiciliario: Domiciliario): void {
    this.editandoDomiciliario = { ...domiciliario };
    this.mostrarFormulario = true;
  }

  /**
   * Cierra el formulario
   */
  cerrarFormulario(): void {
    this.mostrarFormulario = false;
    this.editandoDomiciliario = null;
  }

  /**
   * Manejador cuando se guarda un domiciliario desde el formulario
   */
  onGuardarDomiciliario(datos: DomiciliarioUpsertRequest): void {
    this.formularioEnvio = true;
    this.mensajeError = '';

    if (this.editandoDomiciliario?.id) {
      // Actualizar
      this.domiciliarioService.actualizar(this.editandoDomiciliario.id, datos).subscribe({
        next: () => {
          this.mensajeExito = 'Domiciliario actualizado correctamente.';
          this.cerrarFormulario();
          this.cargarDomiciliarios();
          setTimeout(() => (this.mensajeExito = ''), 3000);
          this.formularioEnvio = false;
        },
        error: (error) => {
          this.mensajeError = error?.error?.message || 'Error al actualizar domiciliario.';
          this.formularioEnvio = false;
        },
      });
    } else {
      // Crear
      this.domiciliarioService.crear(datos).subscribe({
        next: () => {
          this.mensajeExito = 'Domiciliario creado correctamente.';
          this.cerrarFormulario();
          this.cargarDomiciliarios();
          setTimeout(() => (this.mensajeExito = ''), 3000);
          this.formularioEnvio = false;
        },
        error: (error) => {
          this.mensajeError = error?.error?.message || 'Error al crear domiciliario.';
          this.formularioEnvio = false;
        },
      });
    }
  }

  /**
   * Elimina un domiciliario
   */
  onEliminar(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este domiciliario?')) {
      this.domiciliarioService.eliminar(id).subscribe({
        next: () => {
          this.mensajeExito = 'Domiciliario eliminado correctamente.';
          this.cargarDomiciliarios();
          setTimeout(() => (this.mensajeExito = ''), 3000);
        },
        error: (error) => {
          this.mensajeError = error?.error?.message || 'Error al eliminar domiciliario.';
        },
      });
    }
  }

  /**
   * Activa o desactiva un domiciliario
   */
  onToggleActivo(domiciliario: Domiciliario): void {
    const accion = domiciliario.activo ? this.domiciliarioService.desactivar : this.domiciliarioService.activar;
    
    accion.call(this.domiciliarioService, domiciliario.id).subscribe({
      next: () => {
        const estado = domiciliario.activo ? 'desactivado' : 'activado';
        this.mensajeExito = `Domiciliario ${estado} correctamente.`;
        this.cargarDomiciliarios();
        setTimeout(() => (this.mensajeExito = ''), 3000);
      },
      error: (error) => {
        this.mensajeError = error?.error?.message || 'Error al cambiar estado.';
      },
    });
  }

  /**
   * Retorna el color según el estado de activo
   */
  getActivoColor(activo: boolean): string {
    return activo ? '#28a745' : '#dc3545';
  }

  /**
   * Retorna el color según disponibilidad
   */
  getDisponibleColor(disponible: boolean): string {
    return disponible ? '#007bff' : '#ffc107';
  }
}
