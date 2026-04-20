import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Operador } from '../models/operador';

export interface OperadorPayload {
  nombre: string;
  usuario: string;
  contrasena: string;
}

export interface OperadorFormState extends OperadorPayload {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class OperadorService {
  private readonly apiUrl = '/api/operadores';

  constructor(private readonly http: HttpClient) {}

  buildInitialFormState(): OperadorFormState {
    return { id: 0, nombre: '', usuario: '', contrasena: '' };
  }

  mapToFormState(operador: Operador): OperadorFormState {
    return {
      id: operador.id,
      nombre: operador.nombre,
      usuario: operador.usuario,
      contrasena: ''
    };
  }

  buildPayload(form: OperadorFormState): OperadorPayload {
    return {
      nombre: form.nombre,
      usuario: form.usuario,
      contrasena: form.contrasena
    };
  }

  list(): Observable<Operador[]> {
    return this.http.get<Operador[]>(this.apiUrl);
  }

  create(payload: OperadorPayload): Observable<Operador> {
    return this.http.post<Operador>(this.apiUrl, payload);
  }

  update(id: number, payload: OperadorPayload): Observable<Operador> {
    return this.http.put<Operador>(`${this.apiUrl}/${id}`, payload);
  }

  save(form: OperadorFormState): Observable<Operador> {
    const payload = this.buildPayload(form);
    return form.id ? this.update(form.id, payload) : this.create(payload);
  }

  delete(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }
}
