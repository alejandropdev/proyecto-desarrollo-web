import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Categoria } from '../models/categoria';
import { Plato } from '../models/plato';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlatoService {
  private readonly apiUrl = '/api/admin/platos';

  constructor(private readonly http: HttpClient) {}

  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>('/api/categorias');
  }

  getPlatos(params?: { category?: string; q?: string }): Observable<Plato[]> {
    return this.http.get<Plato[]>(this.apiUrl, { params: params ?? {} });
  }

  getPlatoById(id: number): Observable<Plato> {
    return this.http.get<Plato>(`${this.apiUrl}/${id}`);
  }

  savePlato(input: Omit<Plato, 'id' | 'activo'> & { id?: number }): Observable<Plato> {
    const payload = {
      nombre: input.nombre.trim(),
      descripcion: input.descripcion.trim(),
      precio: input.precio,
      imagenUrl: input.imagenUrl.trim(),
      categoriaId: input.categoria?.id ?? null
    };
    if (input.id === undefined) {
      return this.http.post<Plato>(this.apiUrl, payload);
    }
    return this.http.put<Plato>(`${this.apiUrl}/${input.id}`, payload);
  }

  deletePlato(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }
}
