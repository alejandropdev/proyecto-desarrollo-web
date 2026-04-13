import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Producto } from '../models/producto';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Plato } from '../models/plato';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private readonly apiUrl = '/api/productos';

  constructor(private readonly http: HttpClient) { }

  getProductos(params?: { category?: string; q?: string }): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl, { params: params ?? {} });
  }

  getProductosDestacados(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl).pipe(map((productos) => productos.slice(0, 3)));
  }

  getProductoById(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  createProducto(payload: { nombre: string; descripcion: string; precio: number; imagenUrl: string; categoriaId: number }): Observable<Plato> {
    return this.http.post<Plato>(this.apiUrl, payload);
  }

  updateProducto(id: number, payload: { nombre: string; descripcion: string; precio: number; imagenUrl: string; categoriaId: number }): Observable<Plato> {
    return this.http.put<Plato>(`${this.apiUrl}/${id}`, payload);
  }

  deleteProducto(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }
}