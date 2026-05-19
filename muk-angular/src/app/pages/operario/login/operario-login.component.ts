import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { OperarioAuthService } from '../../../services/operario-auth.service';

@Component({
  selector: 'app-operario-login',
  templateUrl: './operario-login.component.html',
  styleUrls: ['./operario-login.component.css']
})
export class OperarioLoginComponent {
  usuario = '';
  password = '';
  error = '';

  constructor(
    private readonly operarioAuthService: OperarioAuthService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.error = '';
    this.operarioAuthService.login(this.usuario, this.password).subscribe({
      next: (session) => {
        this.router.navigate([session.redirectPath || '/operario/pedidos']);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible iniciar sesión.';
      }
    });
  }
}
