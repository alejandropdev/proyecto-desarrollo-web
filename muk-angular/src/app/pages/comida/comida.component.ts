import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { Adicional } from '../../models/adicional';
import { MenuService } from '../../services/menu.service';

@Component({
  selector: 'app-comida',
  templateUrl: './comida.component.html',
  styleUrls: ['./comida.component.css'],
})
export class ComidaComponent implements OnInit {
  producto?: Producto;
  adiciones: Adicional[] = [];
  selectedAdiciones: number[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly menuService: MenuService,
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
      },
    });
  }

  isAdicionSelected(adicionId: number): boolean {
    return this.selectedAdiciones.includes(adicionId);
  }

  toggleAdicion(adicionId: number): void {
    if (this.isAdicionSelected(adicionId)) {
      this.selectedAdiciones = this.selectedAdiciones.filter(
        (id) => id !== adicionId,
      );
      return;
    }
    this.selectedAdiciones = [...this.selectedAdiciones, adicionId];
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('clienteEmail');
  }

  hacerPedido(): void {
    if (!this.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.producto) {
      return;
    }

    // Mapear IDs de adiciones a objetos con información completa
    const adicionesConInfo = this.selectedAdiciones
      .map((adicionId) => this.adiciones.find((a) => a.id === adicionId))
      .filter((a) => a !== undefined) as Adicional[];

    // Guardar en sessionStorage para transferencia segura
    const datosTransferencia = {
      productoId: this.producto.id,
      productoNombre: this.producto.nombre,
      productoPrecio: this.producto.precio,
      productoCategoriaId: this.producto.categoria?.id,
      adicionesPreseleccionadas: adicionesConInfo.map((a) => ({
        id: a.id,
        nombre: a.nombre,
        precio: a.precio,
      })),
    };

    sessionStorage.setItem('datosPedido', JSON.stringify(datosTransferencia));

    // Navegar a crear-pedido
    this.router.navigate(['/pedidos/crear']);
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
      },
    });
  }
}
