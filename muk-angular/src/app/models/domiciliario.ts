export interface Domiciliario {
  id: number;
  nombre: string;
  celular: string;
  cedula: string;
  disponible: boolean;
  activo: boolean;
}

export interface DomiciliarioUpsertRequest {
  nombre: string;
  celular: string;
  cedula: string;
}