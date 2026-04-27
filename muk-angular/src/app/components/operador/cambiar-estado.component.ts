import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Pedido, EstadoPedido } from '../../models/pedido';

/**
 * Componente modal/form para cambiar el estado de un pedido.
 * 
 * Este componente es una alternativa standalone si prefieres usar
 * un diálogo modal en lugar de un select en la tarjeta.
 */
@Component({
  selector: 'app-cambiar-estado',
  templateUrl: './cambiar-estado.component.html',
  styleUrls: ['./cambiar-estado.component.css'],
})
export class CambiarEstadoComponent {
  @Input() pedidoId: number | null = null;
  @Input() estadoActual: string = '';
  @Input() mostrar: boolean = false;

  @Output() confirmar = new EventEmitter<string>();
  @Output() cancelar = new EventEmitter<void>();

  // Estados disponibles
  estadosDisponibles = Object.values(EstadoPedido);

  nuevoEstado: string = '';
  mensajeError: string = '';

  ngOnInit(): void {
    this.nuevoEstado = this.estadoActual;
  }

  /**
   * Confirma el cambio de estado
   */
  onConfirmar(): void {
    if (!this.nuevoEstado || this.nuevoEstado === this.estadoActual) {
      this.mensajeError = 'Selecciona un estado diferente al actual.';
      return;
    }

    this.confirmar.emit(this.nuevoEstado);
    this.limpiar();
  }

  /**
   * Cancela el cambio de estado
   */
  onCancelar(): void {
    this.cancelar.emit();
    this.limpiar();
  }

  /**
   * Limpia los datos del formulario
   */
  private limpiar(): void {
    this.nuevoEstado = this.estadoActual;
    this.mensajeError = '';
  }

  /**
   * Retorna el emoji según el estado
   */
  getEstadoEmoji(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':
        return '⏳';
      case 'EN_PREPARACION':
        return '👨‍🍳';
      case 'LISTO':
        return '✅';
      case 'EN_CAMINO':
        return '🚗';
      case 'COMPLETADO':
        return '🎉';
      case 'CANCELADO':
        return '❌';
      default:
        return '📦';
    }
  }
}
