import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { PedidoDetalle } from '../../../models/pedido';

@Component({
  selector: 'app-detalle-pedido',
  templateUrl: './detalle-pedido.component.html',
  styleUrls: ['./detalle-pedido.component.css'],
})
export class DetallePedidoComponent implements OnInit {
  pedido: PedidoDetalle | null = null;
  isLoading: boolean = true;
  error: string = '';
  pedidoId: number = 0;
  clienteEmail: string = '';

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    // Verificar si el usuario está autenticado
    this.clienteEmail = localStorage.getItem('clienteEmail') || '';
    if (!this.clienteEmail) {
      this.router.navigate(['/login']);
      return;
    }

    // Obtener el ID del pedido de la ruta
    this.route.paramMap.subscribe((params) => {
      this.pedidoId = parseInt(params.get('id') || '0');
      if (this.pedidoId > 0) {
        this.cargarPedido();
      } else {
        this.error = 'ID de pedido inválido.';
        this.isLoading = false;
      }
    });
  }

  cargarPedido(): void {
    this.isLoading = true;
    this.error = '';

    this.pedidoService.obtenerPedidoDetalle(this.pedidoId).subscribe({
      next: (pedido) => {
        this.pedido = pedido;
        this.isLoading = false;
      },
      error: (err) => {
        this.error =
          err?.error?.message ?? 'No se pudo cargar el detalle del pedido.';
        this.isLoading = false;
      },
    });
  }

  volver(): void {
    this.router.navigate(['/pedidos/mis-pedidos']);
  }

  calcularSubtotal(): number {
    if (!this.pedido || !this.pedido.items) return 0;
    return this.pedido.items.reduce((sum, item) => {
      return sum + item.precioUnitario * item.cantidad;
    }, 0);
  }

  calcularTotalAdiciones(): number {
    if (!this.pedido || !this.pedido.items) return 0;
    return this.pedido.items.reduce((sum, item) => {
      const adicionesTotal = item.selecciones.reduce((adSum, adicion) => {
        return adSum + adicion.precio * item.cantidad;
      }, 0);
      return sum + adicionesTotal;
    }, 0);
  }

  calcularTotal(): number {
    return this.calcularSubtotal() + this.calcularTotalAdiciones();
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
