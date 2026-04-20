import { Component, OnInit } from '@angular/core';
import { Pedido } from '../../../models/pedido';
import { PedidoService } from '../../../services/pedido.service';

@Component({
  selector: 'app-pedido-portal',
  templateUrl: './pedido-portal.component.html',
  styleUrls: ['./pedido-portal.component.css']
})
export class PedidoPortalComponent implements OnInit {
  pedidos: Pedido[] = [];
  cargando: boolean = true;

  constructor(private readonly pedidoService: PedidoService) {}

  ngOnInit(): void {
    this.cargarPedidos();
  }

  private cargarPedidos(): void {
    this.cargando = true;
    this.pedidoService.listaPedidos().subscribe({
      next: (pedidos) => {
        this.pedidos = pedidos;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar pedidos:', err);
        this.cargando = false;
      }
    });
  }

  obtenerNombresProductos(pedido: Pedido): string {
    if (!pedido.items || pedido.items.length === 0) {
      return '-';
    }
    return pedido.items
      .map(item => `${item.producto.nombre} (${item.cantidad})`)
      .join(', ');
  }

  obtenerCantidadTotal(pedido: Pedido): number {
    if (!pedido.items) return 0;
    return pedido.items.reduce((total, item) => total + item.cantidad, 0);
  }
}
