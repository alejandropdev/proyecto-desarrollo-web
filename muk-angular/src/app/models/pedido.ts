import { Cliente } from './cliente';
import { ItemCarrito } from './item-carrito';

export interface Operador {
  id: number;
  nombre: string;
  usuario: string;
  activo: boolean;
}

export interface Domiciliario {
  id: number;
  nombre: string;
  celular: string;
  cedula: string;
  disponible: boolean;
}

export interface Pedido {
  id: number;
  cliente: Cliente;
  operador: Operador | null;
  domiciliario: Domiciliario | null;
  estado: string;
  fechaCreacion: string;
  fechaEntrega: string | null;
  items: ItemCarrito[];
}
