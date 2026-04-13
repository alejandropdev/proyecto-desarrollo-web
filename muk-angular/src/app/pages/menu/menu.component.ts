import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { Categoria } from '../../models/categoria';
import { Adicional } from '../../models/adicional';
import { MenuService } from '../../services/menu.service';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  adiciones: Adicional[] = [];
  selectedCategory = '';
  searchQuery = '';
  showAdiciones = false;

  constructor(
    private readonly menuService: MenuService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      this.selectedCategory = params.get('category') ?? '';
      this.searchQuery = params.get('q') ?? '';
      this.loadMenu();
    });
  }

  onSearch(): void {
    this.router.navigate(['/menu'], {
      queryParams: {
        category: this.selectedCategory || null,
        q: this.searchQuery || null
      },
      queryParamsHandling: ''
    });
  }

  filterByCategory(category: string): void {
    this.selectedCategory = category;
    this.onSearch();
  }

  private loadMenu(): void {
    this.menuService
      .getMenu({
        category: this.selectedCategory || undefined,
        q: this.searchQuery || undefined
      })
      .subscribe({
        next: (response) => {
          this.productos = response.productos;
          this.categorias = response.categorias;
          this.adiciones = response.adiciones;
          this.showAdiciones = false;
        },
        error: () => {
          this.productos = [];
          this.categorias = [];
          this.adiciones = [];
          this.showAdiciones = false;
        }
      });
  }
}
