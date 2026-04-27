import { Injectable } from '@angular/core';

/** Estado mínimo de una línea del carrito que se persiste en localStorage. */
export interface LineaCarritoGuardada {
  productoId: number;
  cantidad: number;
  adicionalesSeleccionados: number[];
}

/**
 * Servicio de persistencia del carrito de compras.
 * Guarda y restaura el estado del carrito en localStorage para que
 * el usuario no pierda su selección al recargar la página.
 */
@Injectable({
  providedIn: 'root',
})
export class CarritoService {
  private readonly STORAGE_KEY = 'muk_carrito';

  /** Guarda las líneas del carrito en localStorage. */
  guardar(lineas: LineaCarritoGuardada[]): void {
    const lineasConProducto = lineas.filter(l => l.productoId != null);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(lineasConProducto));
  }

  /** Carga las líneas guardadas desde localStorage. Retorna arreglo vacío si no hay datos. */
  cargar(): LineaCarritoGuardada[] {
    const guardado = localStorage.getItem(this.STORAGE_KEY);
    if (!guardado) { return []; }
    try {
      return JSON.parse(guardado) as LineaCarritoGuardada[];
    } catch {
      return [];
    }
  }

  /** Elimina el carrito guardado (se llama después de confirmar el pedido). */
  limpiar(): void {
    localStorage.removeItem(this.STORAGE_KEY);
  }
}
