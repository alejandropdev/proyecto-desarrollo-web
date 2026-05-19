import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable, map } from 'rxjs';
import { OperarioAuthService } from '../services/operario-auth.service';

@Injectable({
  providedIn: 'root'
})
export class OperarioAuthGuard implements CanActivate {
  constructor(
    private readonly operarioAuthService: OperarioAuthService,
    private readonly router: Router
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.operarioAuthService.isAuthenticated().pipe(
      map((isAuthenticated) => {
        return isAuthenticated ? true : this.router.createUrlTree(['/operario/login']);
      })
    );
  }
}
