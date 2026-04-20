import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ProductoService } from '../../../services/producto.service';
import { AdicionalService } from '../../../services/adicional.service';
import {
  CrearPedidoRequest,
  SeleccionAdicionalRequest,
} from '../../../models/pedido';
import { Producto } from '../../../models/producto';
import { Adicional } from '../../../models/adicional';

@Component({
  selector: 'app-crear-pedido',
  templateUrl: './crear-pedido.component.html',
  styleUrls: ['./crear-pedido.component.css'],
})
export class CrearPedidoComponent implements OnInit {
  productos: Producto[] = [];
  todosLosAdicionales: Adicional[] = [];
  adicionalesFiltrados: Adicional[] = [];
  adicionalesSeleccionados: number[] = [];

  productoSeleccionadoId: number | null = null;
  cantidadProductos: number = 1;

  error: string = '';
  successMessage: string = '';
  isLoading: boolean = false;
  clienteEmail: string = '';

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly productoService: ProductoService,
    private readonly adicionalService: AdicionalService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.clienteEmail = localStorage.getItem('clienteEmail') || '';

    if (!this.clienteEmail) {
      this.router.navigate(['/login']);
      return;
    }

    this.cargarProductos();
    this.cargarAdicionales();
  }

  cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
      },
      error: () => {
        this.error = 'No fue posible cargar productos.';
      },
    });
  }

  cargarAdicionales(): void {
    this.adicionalService.getAdiciones().subscribe({
      next: (data) => {
        this.todosLosAdicionales = data;
      },
      error: () => {
        this.error = 'No fue posible cargar adicionales.';
      },
    });
  }

  onProductoChange(): void {
    if (!this.productoSeleccionadoId) {
      this.adicionalesFiltrados = [];
      return;
    }

    this.adicionalesFiltrados = this.todosLosAdicionales.filter(
      (adicional) =>
        adicional.categoria?.id ===
        this.productos.find((p) => p.id === this.productoSeleccionadoId)
          ?.categoria.id,
    );

    this.adicionalesSeleccionados = [];
  }

  onAdicionalChange(event: any, adicionalId: number): void {
    if (event.target.checked) {
      this.adicionalesSeleccionados.push(adicionalId);
    } else {
      this.adicionalesSeleccionados = this.adicionalesSeleccionados.filter(
        (id) => id !== adicionalId,
      );
    }
  }

  onSubmit(): void {
    this.error = '';
    this.successMessage = '';

    if (!this.productoSeleccionadoId) {
      this.error = 'Debes seleccionar un producto.';
      return;
    }

    this.isLoading = true;

    const adiciones: SeleccionAdicionalRequest[] =
      this.adicionalesSeleccionados.map((id) => ({
        adicionalId: id,
        precio: 0,
      }));

    const request: CrearPedidoRequest = {
      items: [
        {
          productoId: this.productoSeleccionadoId,
          cantidad: this.cantidadProductos,
          adiciones,
        },
      ],
    };

    const clienteId = localStorage.getItem('clienteId') || '1';

    this.pedidoService.crearPedido(parseInt(clienteId), request).subscribe({
      next: () => {
        this.successMessage = 'Pedido creado exitosamente';
        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/pedidos/mis-pedidos']);
        }, 1500);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible crear el pedido.';
        this.isLoading = false;
      },
    });
  }

  onCancel(): void {
    this.router.navigate(['/']);
  }
}
