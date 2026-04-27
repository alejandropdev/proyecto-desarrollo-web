import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { OperarioAuthService } from '../../../services/operario-auth.service';
import { Pedido } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';

type FiltroEstado =
  | 'NO_COMPLETADOS'
  | 'PENDIENTE'
  | 'EN_PREPARACION'
  | 'LISTO'
  | 'EN_CAMINO'
  | 'COMPLETADO'
  | 'CANCELADO';

@Component({
  selector: 'app-pedido-portal',
  templateUrl: './pedido-portal.component.html',
  styleUrls: ['./pedido-portal.component.css'],
})
export class PedidoPortalComponent implements OnInit {
  todosPedidos: Pedido[] = [];
  pedidosFiltrados: Pedido[] = [];
  clientesMap: Map<number, Cliente> = new Map();

  filtroActual: FiltroEstado = 'NO_COMPLETADOS';
  filtrosDisponibles: FiltroEstado[] = [
    'NO_COMPLETADOS',
    'PENDIENTE',
    'EN_PREPARACION',
    'LISTO',
    'EN_CAMINO',
    'COMPLETADO',
    'CANCELADO',
  ];

  isLoading = false;
  mensajeExito = '';
  mensajeError = '';

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly operarioAuthService: OperarioAuthService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.isLoading = true;
    this.mensajeError = '';

    this.pedidoService.listaPedidos().subscribe({
      next: (pedidos) => {
        this.todosPedidos = pedidos;
        this.aplicarFiltro();
        this.cargarClientes(pedidos);
        this.isLoading = false;
      },
      error: () => {
        this.mensajeError = 'No se pudieron cargar los pedidos.';
        this.isLoading = false;
      },
    });
  }

  private cargarClientes(pedidos: Pedido[]): void {
    const ids = [...new Set(pedidos.map((p) => p.clienteId).filter(Boolean))];

    ids.forEach((id) => {
      if (!this.clientesMap.has(id)) {
        this.clienteService.clienteById(id).subscribe({
          next: (cliente) => this.clientesMap.set(id, cliente),
        });
      }
    });
  }

  aplicarFiltro(): void {
    if (this.filtroActual === 'NO_COMPLETADOS') {
      this.pedidosFiltrados = this.todosPedidos.filter(
        (p) => !['COMPLETADO', 'CANCELADO'].includes(p.estado?.toUpperCase()),
      );
      return;
    }

    this.pedidosFiltrados = this.todosPedidos.filter(
      (p) => p.estado?.toUpperCase() === this.filtroActual,
    );
  }

  cambiarFiltro(filtro: FiltroEstado): void {
    this.filtroActual = filtro;
    this.aplicarFiltro();
  }

  cambiarEstado(pedido: Pedido, nuevoEstado: string): void {
    if (!nuevoEstado || pedido.estado === nuevoEstado) return;

    this.isLoading = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.pedidoService.cambiarEstado(pedido.id, nuevoEstado).subscribe({
      next: () => {
        this.mensajeExito = `Pedido #${pedido.id} actualizado a ${this.formatearEstado(nuevoEstado)}.`;
        this.cargarPedidos();
      },
      error: (error) => {
        this.mensajeError =
          error?.error?.message || 'No se pudo cambiar el estado del pedido.';
        this.isLoading = false;
      },
    });
  }

  verDetalle(pedidoId: number): void {
    this.router.navigate(['/pedidos/detalle', pedidoId]);
  }

  cancelarPedido(pedido: Pedido): void {
    this.cambiarEstado(pedido, 'CANCELADO');
  }

  recargar(): void {
    this.cargarPedidos();
  }

  cerrarSesion(): void {
    this.operarioAuthService.logout();
    this.router.navigate(['/operario/login']);
  }

  getNombreCliente(clienteId: number): string {
    const cliente = this.clientesMap.get(clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : `Cliente #${clienteId}`;
  }

  formatearEstado(estado: string): string {
    if (estado === 'NO_COMPLETADOS') return 'No completados';
    if (estado === 'COMPLETADO') return 'Entregado';
    return estado.toLowerCase().replace(/_/g, ' ');
  }

  getEstadoClass(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'PENDIENTE':
        return 'estado pendiente';
      case 'EN_PREPARACION':
        return 'estado preparacion';
      case 'LISTO':
        return 'estado listo';
      case 'EN_CAMINO':
        return 'estado camino';
      case 'COMPLETADO':
        return 'estado completado';
      case 'CANCELADO':
        return 'estado cancelado';
      default:
        return 'estado';
    }
  }
}