import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { ProductoService } from '../../services/producto.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  productosDestacados: Producto[] = [];

  constructor(
    private readonly productoService: ProductoService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentSession().subscribe({
      next: (session) => {
        const redirectPath = session.redirectPath || this.authService.redirectPathFor(session.role);
        if (redirectPath && redirectPath !== '/') {
          this.router.navigateByUrl(redirectPath);
          return;
        }
        this.cargarProductosDestacados();
      },
      error: () => this.cargarProductosDestacados()
    });
  }

  private cargarProductosDestacados(): void {
    this.productoService.getProductosDestacados().subscribe({
      next: (productos) => {
        this.productosDestacados = productos;
      },
      error: () => {
        this.productosDestacados = [];
      }
    });
  }
}