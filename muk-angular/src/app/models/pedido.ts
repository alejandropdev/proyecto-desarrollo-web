import { Producto } from './producto';
import { Cliente } from './cliente';
import { Adicional } from './adicional';

export interface Pedido {
  id: number;
  clienteId: number;
  cantidadProductos: number;
  cantidadAdiciones: number;
  estado: string;
  fechaCreacion: string;
  fechaEntrega?: string;
}

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

export interface ItemPedido {
  id: number;
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  selecciones: SeleccionAdicional[];
}

export interface SeleccionAdicional {
  id: number;
  adicional: Adicional;
  precio: number;
}

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
