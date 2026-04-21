import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { OperarioAuthService } from '../../../services/operario-auth.service';
import { Pedido } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';

@Component({
  selector: 'app-pedido-portal',
  templateUrl: './pedido-portal.component.html',
  styleUrls: ['./pedido-portal.component.css']
})
export class PedidoPortalComponent implements OnInit {
  pedidos: Pedido[] = [];
  clientesMap: Map<number, Cliente> = new Map();
  isLoading: boolean = true; // Antes se llamaba 'cargando', ahora 'isLoading'

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly operarioAuthService: OperarioAuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.isLoading = true;
    this.pedidoService.listaPedidos().subscribe({
      next: (data) => {
        this.pedidos = data;
        this.cargarNombresDeClientes(data);
      },
      error: () => this.isLoading = false
    });
  }

  private cargarNombresDeClientes(pedidos: Pedido[]): void {
    const idsUnicos = [...new Set(pedidos.map(p => p.clienteId))];
    let procesados = 0;

    if (idsUnicos.length === 0) { this.isLoading = false; return; }

    idsUnicos.forEach(id => {
      this.clienteService.clienteById(id).subscribe({
        next: (cliente) => {
          this.clientesMap.set(id, cliente);
          if (++procesados === idsUnicos.length) this.isLoading = false;
        },
        error: () => {
          if (++procesados === idsUnicos.length) this.isLoading = false;
        }
      });
    });
  }

  // Se renombró para que el HTML lo encuentre
  getNombreCliente(pedido: any): string {
    const p = pedido as any;
    if (p.cliente && p.cliente.nombre) {
      return `${p.cliente.nombre} ${p.cliente.apellido}`;
    }
    const enMapa = this.clientesMap.get(pedido.clienteId);
    return enMapa ? `${enMapa.nombre} ${enMapa.apellido}` : `CLIENTE #${pedido.clienteId}`;
  }

  // Se renombró para que el HTML lo encuentre
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

  // Se renombró para que el HTML lo encuentre
  verDetalle(id: number): void {
    this.router.navigate(['/pedidos/detalle', id]);
  }

  cerrarSesion(): void {
    this.operarioAuthService.logout();
    this.router.navigate(['/operario/login']);
  }
}
