import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Domiciliario, DomiciliarioUpsertRequest } from '../models/domiciliario';

/**
 * Servicio para gestión de Domiciliarios
 * 
 * Responsabilidades:
 * - CRUD de domiciliarios
 * - Activar/desactivar domiciliarios
 * - Consultar domiciliarios disponibles
 */
@Injectable({
  providedIn: 'root'
})
export class DomiciliarioService {
  private readonly apiUrl = '/api/domiciliarios';

  constructor(private readonly http: HttpClient) {}

  /**
   * Obtiene todos los domiciliarios
   */
  listarTodos(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(this.apiUrl);
  }

  /**
   * Obtiene un domiciliario por su ID
   */
  obtenerPorId(id: number): Observable<Domiciliario> {
    return this.http.get<Domiciliario>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtiene todos los domiciliarios que están activos y disponibles
   * (pueden recibir nuevas asignaciones de pedidos)
   */
  obtenerActivosDisponibles(): Observable<Domiciliario[]> {
    return this.http.get<Domiciliario[]>(`${this.apiUrl}/activos/disponibles`);
  }

  /**
   * Crea un nuevo domiciliario
   */
  crear(request: DomiciliarioUpsertRequest): Observable<Domiciliario> {
    return this.http.post<Domiciliario>(this.apiUrl, request);
  }

  /**
   * Actualiza un domiciliario existente
   */
  actualizar(id: number, request: DomiciliarioUpsertRequest): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(`${this.apiUrl}/${id}`, request);
  }

  /**
   * Elimina un domiciliario
   */
  eliminar(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }

  /**
   * Activa un domiciliario (marca como activo=true)
   */
  activar(id: number): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(`${this.apiUrl}/${id}/activar`, {});
  }

  /**
   * Desactiva un domiciliario (marca como activo=false)
   */
  desactivar(id: number): Observable<Domiciliario> {
    return this.http.put<Domiciliario>(`${this.apiUrl}/${id}/desactivar`, {});
  }
}
