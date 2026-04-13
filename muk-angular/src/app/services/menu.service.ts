import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto';
import { Categoria } from '../models/categoria';
import { Adicional } from '../models/adicional';

export interface MenuResponse {
  productos: Producto[];
  categorias: Categoria[];
  adiciones: Adicional[];
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  constructor(private readonly http: HttpClient) {}

  getMenu(params?: { category?: string; q?: string }): Observable<MenuResponse> {
    let httpParams = new HttpParams();
    if (params?.category && params.category !== 'undefined') {
      httpParams = httpParams.set('category', params.category);
    }
    if (params?.q && params.q !== 'undefined') {
      httpParams = httpParams.set('q', params.q);
    }
    return this.http.get<MenuResponse>('/api/menu', { params: httpParams });
  }

  getComida(id: number): Observable<Producto> {
    return this.http.get<Producto>(`/api/menu/${id}`);
  }
}
