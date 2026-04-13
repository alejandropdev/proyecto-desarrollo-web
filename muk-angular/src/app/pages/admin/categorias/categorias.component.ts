import { Component, OnInit } from '@angular/core';
import { Categoria } from '../../../models/categoria';
import { CategoriaService } from '../../../services/categoria.service';

@Component({
  selector: 'app-categorias',
  templateUrl: './categorias.component.html',
  styleUrls: ['./categorias.component.css']
})
export class CategoriasComponent implements OnInit {
  categorias: Categoria[] = [];
  nombre = '';

  constructor(private readonly categoriaService: CategoriaService) {}

  ngOnInit(): void {
    this.load();
  }

  create(): void {
    if (!this.nombre.trim()) return;
    this.categoriaService.createCategoria(this.nombre.trim()).subscribe(() => {
      this.nombre = '';
      this.load();
    });
  }

  private load(): void {
    this.categoriaService.getCategorias().subscribe((categorias) => (this.categorias = categorias));
  }
}
