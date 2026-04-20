import { SeleccionAdicional } from './seleccion-adicional';
import { Producto } from './producto';

export interface ItemCarrito {
  id: number;
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  selecciones: SeleccionAdicional[];
}
