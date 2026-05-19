import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuthSession {
  username: string;
  role: string;
  redirectPath: string;
}

export interface LogoutResponse {
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string): Observable<AuthSession> {
    sessionStorage.removeItem('AuthToken');
    return this.http.post<AuthSession>(
      '/api/auth/login',
      { username, password },
      { withCredentials: true }
    );
  }

  currentSession(): Observable<AuthSession> {
    return this.http.get<AuthSession>('/api/auth/me', { withCredentials: true });
  }

  logout(): Observable<LogoutResponse> {
    sessionStorage.removeItem('AuthToken');
    return this.http.post<LogoutResponse>(
      '/api/auth/logout',
      {},
      { withCredentials: true }
    );
  }

  redirectPathFor(role: string | null | undefined): string {
    if (role === 'ROLE_CLIENTE') return '/clientes/perfil';
    if (role === 'ROLE_ADMIN') return '/admin/platos';
    if (role === 'ROLE_OPERADOR') return '/operario/pedidos';
    return '/';
  }
}
