import { Producto } from './producto';

export interface ItemCarrito {
  id: number;
  producto: Producto;
  cantidad: number;
  subtotal: number;
}