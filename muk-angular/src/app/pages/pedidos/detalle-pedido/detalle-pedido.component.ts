import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { PedidoDetalle } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';

@Component({
  selector: 'app-detalle-pedido',
  templateUrl: './detalle-pedido.component.html',
  styleUrls: ['./detalle-pedido.component.css']
})
export class DetallePedidoComponent implements OnInit {
  pedido: PedidoDetalle | null = null;
  cliente: Cliente | null = null;
  isLoading: boolean = true;
  error: string | null = null;

  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.cargarDetalle();
  }

  private cargarDetalle(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id');
    if (!id) {
      this.isLoading = false;
      return;
    }

    this.pedidoService.obtenerPedidoDetalle(Number(id)).subscribe({
      next: (pedido) => {
        this.pedido = pedido;
        if (pedido.cliente?.id) {
          this.cargarCliente(pedido.cliente.id);
        } else {
          this.isLoading = false;
        }
      },
      error: () => this.isLoading = false
    });
  }

  private cargarCliente(clienteId: number): void {
    this.clienteService.clienteById(clienteId).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  volver(): void {
    this.router.navigate(['/operario/pedidos']);
  }

  volverAlPortal(): void {
    this.router.navigate(['/operario/pedidos']);
  }

  calcularSubtotal(): number {
    if (!this.pedido?.items) return 0;
    return this.pedido.items.reduce((sum, item) => sum + (item.precioUnitario * item.cantidad), 0);
  }

  calcularTotalAdiciones(): number {
    if (!this.pedido?.items) return 0;
    return this.pedido.items.reduce((sum, item) => {
      const itemAdiciones = item.selecciones?.reduce((itemSum, sel) => itemSum + (sel.precio * item.cantidad), 0) || 0;
      return sum + itemAdiciones;
    }, 0);
  }

  calcularTotal(): number {
    return this.calcularSubtotal() + this.calcularTotalAdiciones();
  }

  getEstadoColor(estado: string): string {
    const est = estado?.toUpperCase() || '';
    switch (est) {
      case 'PENDIENTE': return '#f2b705';
      case 'EN_PREPARACION': 
      case 'EN_PREPARACIÓN': return '#3b82f6';
      case 'EN_CAMINO': return '#8b5cf6';
      case 'ENTREGADO': return '#34100b';
      case 'CANCELADO': return '#8c0e03';
      default: return '#6b7280';
    }
  }
}