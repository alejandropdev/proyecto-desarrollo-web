import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AdminAuthService } from '../../../services/admin-auth.service';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.css']
})
export class AdminLoginComponent {
  usuario = '';
  password = '';
  error = '';

  constructor(
    private readonly authService: AdminAuthService,
    private readonly router: Router
  ) {}

  onSubmit(): void {
    this.error = '';
    this.authService.login(this.usuario, this.password).subscribe({
      next: () => {
        this.router.navigate(['/admin/platos']);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible iniciar sesión.';
      }
    });
  }
}
