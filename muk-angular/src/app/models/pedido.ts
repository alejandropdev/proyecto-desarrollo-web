import { Producto } from './producto';
import { Cliente } from './cliente';
import { Adicional } from './adicional';

// Pedido en listado (simplificado)
export interface Pedido {
  id: number;
  clienteId: number;
  cantidadProductos: number;
  cantidadAdiciones: number;
  estado: string;
  fechaCreacion: string;
  fechaEntrega?: string;
  domiciliarioId?: number | null;
domiciliarioNombre?: string | null;
domiciliarioDisponible?: boolean | null;
}

// Pedido con detalles completos (incluyendo items)
export interface PedidoDetalle {
  id: number;
  cliente: Cliente;
  cantidadProductos: number;
  cantidadAdiciones: number;
  estado: string;
  fechaCreacion: string;
  fechaEntrega?: string;
  items: ItemPedido[];
}

// Item dentro de un pedido
export interface ItemPedido {
  id: number;
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  selecciones: SeleccionAdicional[];
}

// Selección de adicionales en un item
export interface SeleccionAdicional {
  id: number;
  adicional: Adicional;
  precio: number;
}

// Request para crear pedido
export interface CrearPedidoRequest {
  items: ItemPedidoRequest[];
}

export interface ItemPedidoRequest {
  productoId: number;
  cantidad: number;
  adiciones: SeleccionAdicionalRequest[];
}

export interface SeleccionAdicionalRequest {
  adicionalId: number;
  precio: number;
}

/**
 * Request para cambiar el estado de un pedido
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
