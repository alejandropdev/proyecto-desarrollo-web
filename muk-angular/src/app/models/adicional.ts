import { Categoria } from './categoria';

export interface Adicional {
  id: number;
  nombre: string;
  precio: number;
  activo?: boolean;
  categoria?: Categoria | null;
  categoriaId?: number | null;
}

export interface Domiciliario {
  id: number;
  nombre: string;
  celular: string;
  cedula: string;
  activo: boolean;
  disponible: boolean;
}