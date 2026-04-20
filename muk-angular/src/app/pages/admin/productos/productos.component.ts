import { Component, OnInit } from '@angular/core';
import { Producto } from '../../../models/producto';
import { Categoria } from '../../../models/categoria';
import { ProductoFormState, ProductoService } from '../../../services/producto.service';
import { CategoriaService } from '../../../services/categoria.service';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.css']
})
export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  form: ProductoFormState;

  constructor(
    private readonly productoService: ProductoService,
    private readonly categoriaService: CategoriaService
  ) {
    this.form = this.productoService.buildInitialFormState();
  }

  ngOnInit(): void {
    this.load();
    this.categoriaService.getCategorias().subscribe((categorias) => (this.categorias = categorias));
  }

  edit(producto: Producto): void {
    this.form = this.productoService.mapToFormState(producto);
  }

  save(): void {
    this.productoService.save(this.form).subscribe(() => {
      this.form = this.productoService.buildInitialFormState();
      this.load();
    });
  }

  remove(id: number): void {
    if (!window.confirm('¿Eliminar producto?')) return;
    this.productoService.deleteProducto(id).subscribe(() => this.load());
  }

  private load(): void {
    this.productoService.getProductos().subscribe((productos) => (this.productos = productos));
  }
}
