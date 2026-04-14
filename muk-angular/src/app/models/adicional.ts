import { Categoria } from './categoria';

export interface Adicional {
  id: number;
  nombre: string;
  precio: number;
  activo?: boolean;
  categoria?: Categoria | null;
}