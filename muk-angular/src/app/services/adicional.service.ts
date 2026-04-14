import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Adicional } from '../models/adicional';
import { Categoria } from '../models/categoria';

@Injectable({
  providedIn: 'root'
})
export class AdicionalService {
  private readonly apiUrl = '/api/admin/adiciones';

  constructor(private readonly http: HttpClient) {}

  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>('/api/categorias');
  }

  getAdiciones(): Observable<Adicional[]> {
    return this.http.get<Adicional[]>(this.apiUrl);
  }

  getAdicionById(id: number): Observable<Adicional> {
    return this.http.get<Adicional>(`${this.apiUrl}/${id}`);
  }

  saveAdicion(input: Omit<Adicional, 'id' | 'activo'> & { id?: number }): Observable<Adicional> {
    const payload = {
      nombre: input.nombre.trim(),
      precio: input.precio,
      categoriaId: input.categoria?.id ?? null
    };
    if (input.id === undefined) {
      return this.http.post<Adicional>(this.apiUrl, payload);
    }
    return this.http.put<Adicional>(`${this.apiUrl}/${input.id}`, payload);
  }

  deleteAdicion(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }
}
