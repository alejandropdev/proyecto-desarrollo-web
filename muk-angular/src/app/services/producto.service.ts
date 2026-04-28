import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Producto } from '../models/producto';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Plato } from '../models/plato';
import { Adicional } from '../models/adicional';

export interface ProductoPayload {
  nombre: string;
  descripcion: string;
  precio: number;
  imagenUrl: string;
  categoriaId: number;
}

export interface ProductoFormState extends ProductoPayload {
  id: number;
}

@Injectable({
  providedIn: 'root',
})
export class ProductoService {
  private readonly apiUrl = '/api/productos';

  constructor(private readonly http: HttpClient) {}

  buildInitialFormState(): ProductoFormState {
    return {
      id: 0,
      nombre: '',
      descripcion: '',
      precio: 0,
      imagenUrl: '',
      categoriaId: 0,
    };
  }

  mapToFormState(producto: Producto): ProductoFormState {
    return {
      id: producto.id,
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      precio: producto.precio,
      imagenUrl: producto.imagenUrl,
      categoriaId: producto.categoria.id,
    };
  }

  buildPayload(form: ProductoFormState): ProductoPayload {
    return {
      nombre: form.nombre,
      descripcion: form.descripcion,
      precio: form.precio,
      imagenUrl: form.imagenUrl,
      categoriaId: form.categoriaId,
    };
  }

  getProductos(params?: {
    category?: string;
    q?: string;
  }): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl, { params: params ?? {} });
  }

  getProductosDestacados(): Observable<Producto[]> {
    return this.http
      .get<Producto[]>(this.apiUrl)
      .pipe(map((productos) => productos.slice(0, 3)));
  }

  getProductoById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  createProducto(payload: ProductoPayload): Observable<Plato> {
    return this.http.post<Plato>(this.apiUrl, payload);
  }

  updateProducto(id: number, payload: ProductoPayload): Observable<Plato> {
    return this.http.put<Plato>(`${this.apiUrl}/${id}`, payload);
  }

  save(form: ProductoFormState): Observable<Plato> {
    const payload = this.buildPayload(form);
    return form.id
      ? this.updateProducto(form.id, payload)
      : this.createProducto(payload);
  }

  deleteProducto(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtiene las adiciones permitidas para un producto específico.
   */
  obtenerAdicionalesPermitidos(productoId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(
      `${this.apiUrl}/${productoId}/adiciones-permitidas`,
    );
  }
}
