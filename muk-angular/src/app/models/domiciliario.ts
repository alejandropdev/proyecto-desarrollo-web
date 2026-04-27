/**
 * Modelo para Domiciliario
 * Representa un domiciliario (repartidor) del sistema
 */

export interface Domiciliario {
  id: number;
  nombre: string;
  celular: string;
  cedula: string;
  activo: boolean;
  disponible: boolean;
}

/**
 * Request para crear/actualizar un domiciliario
 */
export interface DomiciliarioUpsertRequest {
  nombre: string;
  celular: string;
  cedula: string;
}

/**
 * Response al cambiar estado de pedido
 */
export interface CambiarEstadoResponse {
  pedidoId: number;
  estadoAnterior: string;
  nuevoEstado: string;
  exito: boolean;
  mensaje: string;
}

/**
 * Request para cambiar estado de pedido
 */
export interface CambiarEstadoPedidoRequest {
  nuevoEstado: string;
}

/**
 * Estados válidos de un pedido
 */
export enum EstadoPedido {
  PENDIENTE = 'PENDIENTE',
  EN_PREPARACION = 'EN_PREPARACION',
  LISTO = 'LISTO',
  EN_CAMINO = 'EN_CAMINO',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO'
}
