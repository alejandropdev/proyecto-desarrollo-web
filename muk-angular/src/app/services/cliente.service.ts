import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente';

interface ClientePayload {
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  direccion: string;
  contrasena: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private readonly apiUrl = '/api/clientes';

  constructor(private readonly http: HttpClient) {}

  login(email: string, password: string): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/login`, { email, password });
  }

  registro(payload: ClientePayload): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/registro`, payload);
  }

  getPerfil(email: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/perfil`, { params: { email } });
  }

  updatePerfil(emailOriginal: string, payload: ClientePayload): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/perfil`, payload, { params: { emailOriginal } });
  }

  deletePerfil(email: string): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/perfil`, { params: { email } });
  }

  list(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }
}
