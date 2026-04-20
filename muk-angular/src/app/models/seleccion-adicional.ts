export interface SeleccionAdicional {
  id: number;
  adicional: {
    id: number;
    nombre: string;
    precio: number;
    activo: boolean;
  };
  precio: number;
}
