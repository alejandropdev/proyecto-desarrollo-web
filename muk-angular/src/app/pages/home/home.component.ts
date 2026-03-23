import { Component, OnInit } from '@angular/core';
import { Producto } from '../../models/producto';
import { ProductoService } from '../../services/producto.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  productosDestacados: Producto[] = [];

  constructor(private productoService: ProductoService) {}

  ngOnInit(): void {
    this.productosDestacados = this.productoService.getProductosDestacados();
  }
}