import { Injectable } from '@angular/core';
import { Categoria } from '../models/categoria';
import { Plato } from '../models/plato';

@Injectable({
  providedIn: 'root'
})
export class PlatoService {
  private readonly categorias: Categoria[] = [
    { id: 1, nombre: 'Hamburguesas' },
    { id: 2, nombre: 'Ramen' },
    { id: 3, nombre: 'Pollo' },
    { id: 4, nombre: 'Postres' }
  ];

  private platos: Plato[] = [
    {
      id: 1,
      nombre: 'HOLSTER BURGER',
      descripcion: 'Hamburguesa gigante para verdaderos valientes.',
      precio: 49900,
      imagenUrl: 'https://images.unsplash.com/photo-1582762147076-6d985d99975a?auto=format&fit=crop&w=1200&q=80',
      activo: true,
      categoria: this.categorias[0]
    },
    {
      id: 2,
      nombre: 'NUCLEAR RAMEN',
      descripcion: 'Ramen de picante extremo que pone a prueba tus limites.',
      precio: 39900,
      imagenUrl: 'https://i.redd.it/brpcqnn2zpv01.jpg',
      activo: true,
      categoria: this.categorias[1]
    },
    {
      id: 3,
      nombre: 'TITAN CHICKEN',
      descripcion: 'Porcion brutal de pollo para los mas atrevidos.',
      precio: 59900,
      imagenUrl: 'https://images.unsplash.com/photo-1748864478869-c99e8436171b?auto=format&fit=crop&w=1200&q=80',
      activo: true,
      categoria: this.categorias[2]
    }
  ];

  getCategorias(): Categoria[] {
    return this.categorias.map((categoria) => ({ ...categoria }));
  }

  getPlatos(): Plato[] {
    return this.platos.filter((plato) => plato.activo).map((plato) => this.clonePlato(plato));
  }

  getPlatoById(id: number): Plato | undefined {
    const plato = this.platos.find((item) => item.id === id && item.activo);
    return plato ? this.clonePlato(plato) : undefined;
  }

  savePlato(input: Omit<Plato, 'id' | 'activo'> & { id?: number }): Plato {
    if (input.id === undefined) {
      const nuevoPlato: Plato = {
        id: this.getNextId(),
        nombre: input.nombre.trim(),
        descripcion: input.descripcion.trim(),
        precio: input.precio,
        imagenUrl: input.imagenUrl.trim(),
        categoria: input.categoria,
        activo: true
      };
      this.platos = [...this.platos, nuevoPlato];
      return this.clonePlato(nuevoPlato);
    }

    const index = this.platos.findIndex((item) => item.id === input.id);
    if (index < 0) {
      throw new Error('Plato no encontrado.');
    }

    const actualizado: Plato = {
      ...this.platos[index],
      nombre: input.nombre.trim(),
      descripcion: input.descripcion.trim(),
      precio: input.precio,
      imagenUrl: input.imagenUrl.trim(),
      categoria: input.categoria
    };
    this.platos[index] = actualizado;
    return this.clonePlato(actualizado);
  }

  deletePlato(id: number): void {
    this.platos = this.platos.map((plato) =>
      plato.id === id ? { ...plato, activo: false } : plato
    );
  }

  private getNextId(): number {
    return this.platos.reduce((maxId, plato) => Math.max(maxId, plato.id), 0) + 1;
  }

  private clonePlato(plato: Plato): Plato {
    return {
      ...plato,
      categoria: plato.categoria ? { ...plato.categoria } : null
    };
  }
}
