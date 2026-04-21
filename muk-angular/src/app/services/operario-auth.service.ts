import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface OperarioLoginResponse {
  message: string;
  id: number;
  usuario: string;
  nombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class OperarioAuthService {
  private readonly storageIdKey = 'operarioId';
  private readonly storageUsuarioKey = 'operarioUsuario';
  private readonly storageNombreKey = 'operarioNombre';

  constructor(private readonly http: HttpClient) {}

  login(usuario: string, password: string): Observable<OperarioLoginResponse> {
    return this.http
      .post<OperarioLoginResponse>('/api/operadores/login', { usuario, password })
      .pipe(
        tap((response) => {
          localStorage.setItem(this.storageIdKey, response.id.toString());
          localStorage.setItem(this.storageUsuarioKey, response.usuario);
          localStorage.setItem(this.storageNombreKey, response.nombre);
        })
      );
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.storageIdKey);
  }

  logout(): void {
    localStorage.removeItem(this.storageIdKey);
    localStorage.removeItem(this.storageUsuarioKey);
    localStorage.removeItem(this.storageNombreKey);
  }
}
