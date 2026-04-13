import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Operador } from '../models/operador';

interface OperadorPayload {
  nombre: string;
  usuario: string;
  contrasena: string;
}

@Injectable({
  providedIn: 'root'
})
export class OperadorService {
  private readonly apiUrl = '/api/operadores';

  constructor(private readonly http: HttpClient) {}

  list(): Observable<Operador[]> {
    return this.http.get<Operador[]>(this.apiUrl);
  }

  create(payload: OperadorPayload): Observable<Operador> {
    return this.http.post<Operador>(this.apiUrl, payload);
  }

  update(id: number, payload: OperadorPayload): Observable<Operador> {
    return this.http.put<Operador>(`${this.apiUrl}/${id}`, payload);
  }

  delete(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }
}
