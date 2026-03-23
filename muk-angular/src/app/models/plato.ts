import { Categoria } from './categoria';

export interface Plato {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  imagenUrl: string;
  activo: boolean;
  categoria: Categoria | null;
}
