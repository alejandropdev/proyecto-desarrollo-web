import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Domiciliario, DomiciliarioUpsertRequest } from '../../models/domiciliario';

/**
 * Componente formulario para crear/editar domiciliarios.
 * 
 * Responsabilidades:
 * - Mostrar formulario para capturar datos del domiciliario
 * - Validar campos
 * - Emitir evento de guardado o cancelación
 */
@Component({
  selector: 'app-domiciliario-form',
  templateUrl: './domiciliario-form.component.html',
  styleUrls: ['./domiciliario-form.component.css'],
})
export class DomiciliarioFormComponent implements OnInit {
  @Input() editando: Domiciliario | null = null;
  @Input() enviando: boolean = false;

  @Output() guardar = new EventEmitter<DomiciliarioUpsertRequest>();
  @Output() cancelar = new EventEmitter<void>();

  // Datos del formulario
  nombre: string = '';
  celular: string = '';
  cedula: string = '';

  // Validaciones
  errores: { [key: string]: string } = {};
  formularioValido: boolean = false;

  ngOnInit(): void {
    if (this.editando) {
      // Cargar datos para editar
      this.nombre = this.editando.nombre;
      this.celular = this.editando.celular;
      this.cedula = this.editando.cedula;
    }
    this.validarFormulario();
  }

  /**
   * Valida el formulario
   */
  validarFormulario(): void {
    this.errores = {};
    this.formularioValido = true;

    // Validar nombre
    if (!this.nombre || this.nombre.trim().length === 0) {
      this.errores['nombre'] = 'El nombre es requerido';
      this.formularioValido = false;
    } else if (this.nombre.trim().length < 3) {
      this.errores['nombre'] = 'El nombre debe tener al menos 3 caracteres';
      this.formularioValido = false;
    }

    // Validar celular
    if (!this.celular || this.celular.trim().length === 0) {
      this.errores['celular'] = 'El celular es requerido';
      this.formularioValido = false;
    } else if (!/^\d{10}$/.test(this.celular.replace(/\D/g, ''))) {
      this.errores['celular'] = 'El celular debe tener 10 dígitos';
      this.formularioValido = false;
    }

    // Validar cédula
    if (!this.cedula || this.cedula.trim().length === 0) {
      this.errores['cedula'] = 'La cédula es requerida';
      this.formularioValido = false;
    } else if (!/^\d{8,10}$/.test(this.cedula.replace(/\D/g, ''))) {
      this.errores['cedula'] = 'La cédula debe tener 8-10 dígitos';
      this.formularioValido = false;
    }
  }

  /**
   * Manejador de cambio en los inputs
   */
  onInputChange(): void {
    this.validarFormulario();
  }

  /**
   * Envía el formulario
   */
  onSubmit(): void {
    this.validarFormulario();

    if (!this.formularioValido) {
      return;
    }

    const request: DomiciliarioUpsertRequest = {
      nombre: this.nombre.trim(),
      celular: this.celular.trim(),
      cedula: this.cedula.trim(),
    };

    this.guardar.emit(request);
  }

  /**
   * Cancela el formulario
   */
  onCancelar(): void {
    this.cancelar.emit();
  }

  /**
   * Retorna si un campo tiene error
   */
  tieneError(campo: string): boolean {
    return !!this.errores[campo];
  }

  /**
   * Retorna el mensaje de error de un campo
   */
  getMensajeError(campo: string): string {
    return this.errores[campo] || '';
  }
}
