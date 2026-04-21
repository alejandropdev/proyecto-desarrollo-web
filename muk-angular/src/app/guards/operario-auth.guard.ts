import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { OperarioAuthService } from '../services/operario-auth.service';

@Injectable({
  providedIn: 'root'
})
export class OperarioAuthGuard implements CanActivate {
  constructor(
    private readonly operarioAuthService: OperarioAuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean {
    if (this.operarioAuthService.isAuthenticated()) {
      return true;
    }

    this.router.navigate(['/operario/login']);
    return false;
  }
}
