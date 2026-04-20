import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Categoria } from '../models/categoria';
import { Observable } from 'rxjs';

export interface CategoriaCreateInput {
  nombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {
  private readonly apiUrl = '/api/categorias';

  constructor(private readonly http: HttpClient) { }

  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }

  buildCreatePayload(input: CategoriaCreateInput): CategoriaCreateInput {
    return { nombre: input.nombre.trim() };
  }

  createCategoria(input: CategoriaCreateInput): Observable<Categoria> {
    const payload = this.buildCreatePayload(input);
    return this.http.post<Categoria>(this.apiUrl, payload);
  }
}