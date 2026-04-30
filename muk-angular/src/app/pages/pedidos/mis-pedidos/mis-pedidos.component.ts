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

  // Estados que se consideran "en curso"
  private readonly ESTADOS_EN_CURSO = ['PENDIENTE', 'EN_PREPARACION', 'LISTO', 'EN_CAMINO'];
  // Estados que se consideran "finalizados"
  private readonly ESTADOS_FINALIZADOS = ['COMPLETADO', 'CANCELADO'];

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
      case 'EN_PREPARACION':
        return '#f27405';
      case 'LISTO':
        return '#17a2b8';
      case 'EN_CAMINO':
        return '#007bff';
      case 'COMPLETADO':
        return '#34100b';
      case 'CANCELADO':
        return '#8c0e03';
      default:
        return '#34100b';
    }
  }

  /**
   * Obtiene los pedidos que están en curso
   * @returns Array de pedidos con estado en curso
   */
  getPedidosEnCurso(): Pedido[] {
    return this.pedidos.filter((pedido) =>
      this.ESTADOS_EN_CURSO.includes(pedido.estado.toUpperCase()),
    );
  }

  /**
   * Obtiene los pedidos que han sido finalizados
   * @returns Array de pedidos con estado finalizado
   */
  getPedidosFinalizados(): Pedido[] {
    return this.pedidos.filter((pedido) =>
      this.ESTADOS_FINALIZADOS.includes(pedido.estado.toUpperCase()),
    );
  }

  /**
   * Verifica si hay pedidos en curso
   * @returns true si existen pedidos en curso
   */
  hayPedidosEnCurso(): boolean {
    return this.getPedidosEnCurso().length > 0;
  }

  /**
   * Verifica si hay pedidos finalizados
   * @returns true si existen pedidos finalizados
   */
  hayPedidosFinalizados(): boolean {
    return this.getPedidosFinalizados().length > 0;
  }
}
