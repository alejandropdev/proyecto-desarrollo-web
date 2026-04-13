import { Component, OnInit } from '@angular/core';
import { Producto } from '../../../models/producto';
import { Categoria } from '../../../models/categoria';
import { ProductoService } from '../../../services/producto.service';
import { CategoriaService } from '../../../services/categoria.service';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.css']
})
export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  form = { id: 0, nombre: '', descripcion: '', precio: 0, imagenUrl: '', categoriaId: 0 };

  constructor(
    private readonly productoService: ProductoService,
    private readonly categoriaService: CategoriaService
  ) {}

  ngOnInit(): void {
    this.load();
    this.categoriaService.getCategorias().subscribe((categorias) => (this.categorias = categorias));
  }

  edit(producto: Producto): void {
    this.form = {
      id: producto.id,
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      precio: producto.precio,
      imagenUrl: producto.imagenUrl,
      categoriaId: producto.categoria.id
    };
  }

  save(): void {
    const payload = {
      nombre: this.form.nombre,
      descripcion: this.form.descripcion,
      precio: this.form.precio,
      imagenUrl: this.form.imagenUrl,
      categoriaId: this.form.categoriaId
    };
    const req = this.form.id
      ? this.productoService.updateProducto(this.form.id, payload)
      : this.productoService.createProducto(payload);
    req.subscribe(() => {
      this.form = { id: 0, nombre: '', descripcion: '', precio: 0, imagenUrl: '', categoriaId: 0 };
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
