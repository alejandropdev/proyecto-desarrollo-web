import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Producto } from '../../models/producto';
import { MenuService } from '../../services/menu.service';

@Component({
  selector: 'app-comida',
  templateUrl: './comida.component.html',
  styleUrls: ['./comida.component.css']
})
export class ComidaComponent implements OnInit {
  producto?: Producto;

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
      },
      error: () => {
        this.router.navigate(['/not-found']);
      }
    });
  }
}
