import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Domiciliario } from '../models/domiciliario';

@Injectable({
  providedIn: 'root',
})
export class DomiciliarioService {
  private readonly apiUrl = '/api/domiciliarios';

  constructor(private readonly http: HttpClient) {}

  /** Obtiene todos los domiciliarios. */
  listar(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(this.apiUrl);
  }

  /** Obtiene solo los domiciliarios disponibles para asignar a un envío. */
  listarDisponibles(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(`${this.apiUrl}/disponibles`);
  }

  /** Registra un nuevo domiciliario. */
  crear(data: { nombre: string; celular: string; cedula: string }): Observable<Domiciliario> {
    return this.http.post<Domiciliario>(this.apiUrl, data);
  }

  /** Actualiza los datos de un domiciliario. */
  actualizar(id: number, data: { nombre: string; celular: string; cedula: string }): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(`${this.apiUrl}/${id}`, data);
  }

  /** Desactiva un domiciliario (no trabaja hoy). */
  desactivar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /** Activa un domiciliario (vuelve a estar disponible). */
  activar(id: number): Observable<Domiciliario> {
    return this.http.patch<Domiciliario>(`${this.apiUrl}/${id}/activar`, {});
  }
}
