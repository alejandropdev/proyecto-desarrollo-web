import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Adicional } from '../models/adicional';
import { Categoria } from '../models/categoria';

export interface AdicionFormModel {
  id?: number;
  nombre: string;
  categoriaId: number | null;
  precio: number | null;
}

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

  /** Adiciones activas de una categoría (API pública para formularios de producto). */
  getAdicionesPorCategoria(categoriaId: number): Observable<Adicional[]> {
    return this.http.get<Adicional[]>('/api/adiciones', { params: { categoriaId: String(categoriaId) } });
  }

  getAdicionById(id: number): Observable<Adicional> {
    return this.http.get<Adicional>(`${this.apiUrl}/${id}`);
  }

  buildInitialFormData(): AdicionFormModel {
    return {
      nombre: '',
      categoriaId: null,
      precio: null
    };
  }

  mapToFormData(adicion: Adicional): AdicionFormModel {
    return {
      id: adicion.id,
      nombre: adicion.nombre,
      categoriaId: adicion.categoria?.id ?? null,
      precio: adicion.precio
    };
  }

  saveAdicionFromForm(formData: AdicionFormModel): Observable<Adicional> {
    const payload = {
      nombre: formData.nombre.trim(),
      precio: formData.precio,
      categoriaId: formData.categoriaId
    };
    if (formData.id === undefined) {
      return this.http.post<Adicional>(this.apiUrl, payload);
    }
    return this.http.put<Adicional>(`${this.apiUrl}/${formData.id}`, payload);
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
