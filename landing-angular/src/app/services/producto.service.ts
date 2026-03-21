import { Injectable } from '@angular/core';
import { Producto } from '../models/producto';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {

  private productos: Producto[] = [
    {
      id: 1,
      nombre: 'hOLSTER BURGER',
      descripcion: 'Hamburguesa gigante para verdaderos valientes.',
      precio: 49900,
      imagenUrl: 'https://images.unsplash.com/photo-1582762147076-6d985d99975a?auto=format&fit=crop&w=1200&q=80',
      disponible: true,
      categoria: { id: 1, nombre: 'Hamburguesas' }
    },
    {
      id: 2,
      nombre: 'Nuclear Ramen',
      descripcion: 'Ramen de picante extremo que pone a prueba tus límites.',
      precio: 39900,
      imagenUrl: 'https://i.redd.it/brpcqnn2zpv01.jpg',
      disponible: true,
      categoria: { id: 2, nombre: 'Ramen' }
    },
    {
      id: 3,
      nombre: 'Titan Chicken',
      descripcion: 'Porción brutal de pollo para los más atrevidos.',
      precio: 59900,
      imagenUrl: 'https://images.unsplash.com/photo-1748864478869-c99e8436171b?auto=format&fit=crop&w=1200&q=80',
      disponible: true,
      categoria: { id: 3, nombre: 'Pollo' }
    }
  ];

  constructor() { }

  getProductos(): Producto[] {
    return this.productos;
  }

  getProductosDestacados(): Producto[] {
    return this.productos.slice(0, 3);
  }

  getProductoById(id: number): Producto | undefined {
    return this.productos.find(producto => producto.id === id);
  }
}