import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { Pedido } from '../../../models/pedido';

@Component({
  selector: 'app-mis-pedidos',
  templateUrl: './mis-pedidos.component.html',
  styleUrls: ['./mis-pedidos.component.css'],
})
export class MisPedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  isLoading: boolean = true;
  error: string = '';
  clienteEmail: string = '';
  clienteId: string = '';

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    // Verificar si el usuario está autenticado
    this.clienteEmail = localStorage.getItem('clienteEmail') || '';
    this.clienteId = localStorage.getItem('clienteId') || '';

    if (!this.clienteEmail || !this.clienteId) {
      this.router.navigate(['/login']);
      return;
    }

    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.isLoading = true;
    this.error = '';

    this.pedidoService
      .obtenerPedidosPorCliente(parseInt(this.clienteId))
      .subscribe({
        next: (pedidos) => {
          this.pedidos = pedidos;
          this.isLoading = false;
        },
        error: (err) => {
          this.error = 'No se pudieron cargar los pedidos.';
          this.isLoading = false;
        },
      });
  }

  verDetalle(pedidoId: number): void {
    this.router.navigate(['/pedidos/detalle', pedidoId]);
  }

  crearNuevoPedido(): void {
    this.router.navigate(['/carrito']);
  }

  getEstadoColor(estado: string): string {
    switch (estado.toUpperCase()) {
      case 'PENDIENTE':
        return '#f2b705';
      case 'ENTREGADO':
        return '#34100b';
      case 'CANCELADO':
        return '#8c0e03';
      default:
        return '#34100b';
    }
  }
}
