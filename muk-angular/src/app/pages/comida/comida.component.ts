import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { Adicional } from '../../models/adicional';
import { MenuService } from '../../services/menu.service';

@Component({
  selector: 'app-comida',
  templateUrl: './comida.component.html',
  styleUrls: ['./comida.component.css']
})
export class ComidaComponent implements OnInit {
  producto?: Producto;
  adiciones: Adicional[] = [];
  selectedAdiciones: number[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly menuService: MenuService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (Number.isNaN(id) || id <= 0) {
      this.router.navigate(['/menu']);
      return;
    }
    this.menuService.getComida(id).subscribe({
      next: (producto) => {
        this.producto = producto;
        this.loadAdicionesForCategoria(producto.categoria?.nombre);
      },
      error: () => {
        this.router.navigate(['/not-found']);
      }
    });
  }

  isAdicionSelected(adicionId: number): boolean {
    return this.selectedAdiciones.includes(adicionId);
  }

  toggleAdicion(adicionId: number): void {
    if (this.isAdicionSelected(adicionId)) {
      this.selectedAdiciones = this.selectedAdiciones.filter((id) => id !== adicionId);
      return;
    }
    this.selectedAdiciones = [...this.selectedAdiciones, adicionId];
  }

  private loadAdicionesForCategoria(categoryName?: string): void {
    this.selectedAdiciones = [];
    if (!categoryName) {
      this.adiciones = [];
      return;
    }
    this.menuService.getMenu({ category: categoryName }).subscribe({
      next: (response) => {
        this.adiciones = response.adiciones;
      },
      error: () => {
        this.adiciones = [];
      }
    });
  }
}
