import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Domiciliario } from '../models/domiciliario';

export interface DomiciliarioUpsertRequest {
  nombre: string;
  celular: string;
  cedula: string;
}

@Injectable({
  providedIn: 'root',
})
export class DomiciliarioService {
  private readonly apiUrl = '/api/domiciliarios';

  constructor(private readonly http: HttpClient) {}

  listarTodos(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(this.apiUrl);
  }

  listar(): Observable<Domiciliario[]> {
    return this.listarTodos();
  }

  listarDisponibles(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(`${this.apiUrl}/disponibles`);
  }

  obtenerActivosDisponibles(): Observable<Domiciliario[]> {
    return this.listarDisponibles();
  }

  crear(data: DomiciliarioUpsertRequest): Observable<Domiciliario> {
    return this.http.post<Domiciliario>(this.apiUrl, data);
  }

  actualizar(id: number, data: DomiciliarioUpsertRequest): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(`${this.apiUrl}/${id}`, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  activar(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/desactivar`, {});
  }
}