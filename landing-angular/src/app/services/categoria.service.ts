import { Injectable } from '@angular/core';
import { Categoria } from '../models/categoria';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private categorias: Categoria[] = [
    { id: 1, nombre: 'Hamburguesas' },
    { id: 2, nombre: 'Ramen' },
    { id: 3, nombre: 'Pollo' }
  ];

  constructor() { }

  getCategorias(): Categoria[] {
    return this.categorias;
  }
}