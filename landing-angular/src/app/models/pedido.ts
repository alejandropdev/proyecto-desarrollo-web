
import { Cliente } from './cliente';
import { ItemCarrito } from './item-carrito';

export interface Pedido {
  id: number;
  cliente: Cliente;
  fecha: string;
  estado: string;
  total: number;
  items: ItemCarrito[];
}