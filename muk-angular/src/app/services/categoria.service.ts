import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Categoria } from '../models/categoria';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {
  private readonly apiUrl = '/api/categorias';

  constructor(private readonly http: HttpClient) { }

  getCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }

  createCategoria(nombre: string): Observable<Categoria> {
    return this.http.post<Categoria>(this.apiUrl, { nombre });
  }
}