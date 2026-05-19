import { Injectable } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { AuthService, AuthSession, LogoutResponse } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class OperarioAuthService {
  private readonly storageIdKey = 'operarioId';
  private readonly storageUsuarioKey = 'operarioUsuario';
  private readonly storageNombreKey = 'operarioNombre';

  constructor(private readonly authService: AuthService) {}

  login(usuario: string, password: string): Observable<AuthSession> {
    return this.authService.login(usuario, password).pipe(
      tap((session) => {
        localStorage.setItem(this.storageUsuarioKey, session.username);
      })
    );
  }

  isAuthenticated(): Observable<boolean> {
    return this.authService.currentSession().pipe(
      map((session) => session.role === 'ROLE_OPERADOR'),
      catchError(() => of(false))
    );
  }

  logout(): Observable<LogoutResponse> {
    this.clearLocalState();
    return this.authService.logout().pipe(
      tap(() => this.clearLocalState()),
      catchError((error) => {
        this.clearLocalState();
        throw error;
      })
    );
  }

  clearLocalState(): void {
    localStorage.removeItem(this.storageIdKey);
    localStorage.removeItem(this.storageUsuarioKey);
    localStorage.removeItem(this.storageNombreKey);
  }
}
