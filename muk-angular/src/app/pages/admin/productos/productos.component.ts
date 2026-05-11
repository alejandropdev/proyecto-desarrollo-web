import { Component, OnInit } from '@angular/core';
import { Producto } from '../../../models/producto';
import { Categoria } from '../../../models/categoria';
import { Adicional } from '../../../models/adicional';
import { ProductoFormState, ProductoService } from '../../../services/producto.service';
import { CategoriaService } from '../../../services/categoria.service';
import { AdicionalService } from '../../../services/adicional.service';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.css']
})
export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  categorias: Categoria[] = [];
  adicionesDisponibles: Adicional[] = [];
  form: ProductoFormState;

  constructor(
    private readonly productoService: ProductoService,
    private readonly categoriaService: CategoriaService,
    private readonly adicionalService: AdicionalService
  ) {
    this.form = this.productoService.buildInitialFormState();
  }

  ngOnInit(): void {
    this.load();
    this.categoriaService.getCategorias().subscribe((categorias) => (this.categorias = categorias));
  }

  onCategoriaChange(): void {
    this.form.adicionalesPermitidosIds = [];
    this.refreshAdicionesDisponibles();
  }

  edit(producto: Producto): void {
    this.form = this.productoService.mapToFormState(producto);
    this.refreshAdicionesDisponibles();
  }

  toggleAdicional(id: number): void {
    const idx = this.form.adicionalesPermitidosIds.indexOf(id);
    if (idx >= 0) {
      this.form.adicionalesPermitidosIds.splice(idx, 1);
    } else {
      this.form.adicionalesPermitidosIds.push(id);
    }
  }

  isAdicionalSelected(id: number): boolean {
    return this.form.adicionalesPermitidosIds.includes(id);
  }

  save(): void {
    this.productoService.save(this.form).subscribe(() => {
      this.form = this.productoService.buildInitialFormState();
      this.adicionesDisponibles = [];
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

  private refreshAdicionesDisponibles(): void {
    const cid = this.form.categoriaId;
    if (!cid) {
      this.adicionesDisponibles = [];
      return;
    }
    this.adicionalService.getAdicionesPorCategoria(cid).subscribe((a) => (this.adicionesDisponibles = a));
  }
}
