import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Pedido, EstadoPedido } from '../../models/pedido';
/**
 * Componente para mostrar una tarjeta individual de pedido.
 * 
 * Responsabilidades:
 * - Mostrar información resumida del pedido
 * - Permitir cambiar el estado del pedido
 * - Emitir evento cuando se cambia el estado
 */
@Component({
  selector: 'app-pedido-card',
  templateUrl: './pedido-card.component.html',
  styleUrls: ['./pedido-card.component.css'],
})
export class PedidoCardComponent {
  @Input() pedido!: Pedido;

  // Evento emitido cuando se cambia el estado
  @Output() cambiarEstadoEvent = new EventEmitter<{ pedidoId: number; nuevoEstado: string }>();

  // Estados visibles (no mostrar CANCELADO como opción)
  estadosDisponibles = Object.values(EstadoPedido).filter(e => e !== EstadoPedido.CANCELADO);

  /**
   * Retorna el color de fondo según el estado del pedido
   */
  getEstadoColor(): string {
    switch (this.pedido.estado) {
      case 'PENDIENTE':
        return '#ffc107'; // Amarillo
      case 'EN_PREPARACION':
        return '#17a2b8'; // Cian
      case 'LISTO':
        return '#28a745'; // Verde
      case 'EN_CAMINO':
        return '#007bff'; // Azul
      case 'COMPLETADO':
        return '#20c997'; // Verde más claro
      case 'CANCELADO':
        return '#dc3545'; // Rojo
      default:
        return '#6c757d'; // Gris
    }
  }

  /**
   * Retorna el emoji según el estado
   */
  getEstadoEmoji(): string {
    switch (this.pedido.estado) {
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

  /**
   * Emite el evento de cambiar estado
   */
  onCambiarEstado(nuevoEstado: string): void {
    if (this.pedido.id && nuevoEstado) {
      this.cambiarEstadoEvent.emit({
        pedidoId: this.pedido.id,
        nuevoEstado,
      });
    }
  }

  /**
   * Formatea la fecha a un formato legible
   */
  formatearFecha(fecha?: string): string {
    if (!fecha) return 'N/A';
    
    const date = new Date(fecha);
    return date.toLocaleString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
