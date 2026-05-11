import { Categoria } from './categoria';

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  imagenUrl: string;
  activo: boolean;
  categoria: Categoria;
  adicionalesPermitidosIds?: number[];
}