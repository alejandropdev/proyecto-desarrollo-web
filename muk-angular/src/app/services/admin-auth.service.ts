import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminAuthService {
  constructor(private readonly http: HttpClient) {}

  login(usuario: string, password: string): Observable<{ message: string; usuario: string }> {
    return this.http.post<{ message: string; usuario: string }>('/api/admin/login', { usuario, password });
  }
}
