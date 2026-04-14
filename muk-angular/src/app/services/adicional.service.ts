import { Injectable } from '@angular/core';
import { Adicional } from '../models/adicional';

type StoredAdicional = Adicional & { activo: boolean };

@Injectable({
  providedIn: 'root'
})
export class AdicionalService {
  private adiciones: StoredAdicional[] = [
    { id: 1, nombre: 'Queso extra', precio: 3500, activo: true },
    { id: 2, nombre: 'Tocineta crujiente', precio: 4500, activo: true },
    { id: 3, nombre: 'Salsa de la casa', precio: 2000, activo: true }
  ];

  getAdiciones(): Adicional[] {
    return this.adiciones
      .filter((adicion) => adicion.activo)
      .map((adicion) => this.cloneAdicion(adicion));
  }

  getAdicionById(id: number): Adicional | undefined {
    const adicion = this.adiciones.find((item) => item.id === id && item.activo);
    return adicion ? this.cloneAdicion(adicion) : undefined;
  }

  saveAdicion(input: Omit<Adicional, 'id'> & { id?: number }): Adicional {
    if (input.id === undefined) {
      const nuevaAdicion: StoredAdicional = {
        id: this.getNextId(),
        nombre: input.nombre.trim(),
        precio: input.precio,
        activo: true
      };
      this.adiciones = [...this.adiciones, nuevaAdicion];
      return this.cloneAdicion(nuevaAdicion);
    }

    const index = this.adiciones.findIndex((item) => item.id === input.id);
    if (index < 0) {
      throw new Error('Adicion no encontrada.');
    }

    const actualizada: StoredAdicional = {
      ...this.adiciones[index],
      nombre: input.nombre.trim(),
      precio: input.precio
    };
    this.adiciones[index] = actualizada;
    return this.cloneAdicion(actualizada);
  }

  deleteAdicion(id: number): void {
    this.adiciones = this.adiciones.map((adicion) =>
      adicion.id === id ? { ...adicion, activo: false } : adicion
    );
  }

  private getNextId(): number {
    return this.adiciones.reduce((maxId, adicion) => Math.max(maxId, adicion.id), 0) + 1;
  }

  private cloneAdicion(adicion: Adicional): Adicional {
    return { ...adicion };
  }
}
