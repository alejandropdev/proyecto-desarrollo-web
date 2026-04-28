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

  /**
   * Agrega una línea al carrito manteniendo las que ya existan.
   * Si ya existe una línea con mismo producto y mismas adiciones, suma la cantidad.
   */
  agregarLinea(nuevaLinea: LineaCarritoGuardada): void {
    const lineas = this.cargar();
    const adicionalesNormalizados = [...(nuevaLinea.adicionalesSeleccionados ?? [])].sort((a, b) => a - b);

    const indiceExistente = lineas.findIndex((linea) => {
      if (linea.productoId !== nuevaLinea.productoId) {
        return false;
      }
      const actuales = [...(linea.adicionalesSeleccionados ?? [])].sort((a, b) => a - b);
      if (actuales.length !== adicionalesNormalizados.length) {
        return false;
      }
      return actuales.every((id, index) => id === adicionalesNormalizados[index]);
    });

    if (indiceExistente >= 0) {
      lineas[indiceExistente].cantidad += nuevaLinea.cantidad;
    } else {
      lineas.push({
        productoId: nuevaLinea.productoId,
        cantidad: nuevaLinea.cantidad,
        adicionalesSeleccionados: adicionalesNormalizados,
      });
    }

    this.guardar(lineas);
  }
}
