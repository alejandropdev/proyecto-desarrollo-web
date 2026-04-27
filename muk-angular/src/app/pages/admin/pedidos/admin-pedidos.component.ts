import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { ProductoService } from '../../../services/producto.service';
import { Pedido } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';
import { Producto } from '../../../models/producto';

@Component({
  selector: 'app-admin-pedidos',
  templateUrl: './admin-pedidos.component.html',
})
export class AdminPedidosComponent implements OnInit {
  /** Todos los pedidos cargados del sistema. */
  todosPedidos: Pedido[] = [];
  /** Pedidos que se muestran tras aplicar filtros. */
  pedidosFiltrados: Pedido[] = [];

  clientesMap: Map<number, Cliente> = new Map();
  productos: Producto[] = [];

  isLoading: boolean = true;

  // Filtros
  filtroEstado: string = '';
  filtroFechaDesde: string = '';
  filtroFechaHasta: string = '';
  filtroProductoId: number | null = null;

  readonly estados = ['PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO'];

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly productoService: ProductoService,
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarPedidos();
  }

  private cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => { this.productos = data; },
      error: () => { this.productos = []; }
    });
  }

  cargarPedidos(productoId?: number): void {
    this.isLoading = true;
    const obs = productoId
      ? this.pedidoService.listarTodos(productoId)
      : this.pedidoService.listarTodos();

    obs.subscribe({
      next: (data) => {
        this.todosPedidos = data;
        this.aplicarFiltros();
        this.cargarNombresClientes(data);
      },
      error: () => { this.isLoading = false; }
    });
  }

  private cargarNombresClientes(pedidos: Pedido[]): void {
    const idsUnicos = [...new Set(pedidos.map(p => p.clienteId))];
    let procesados = 0;

    if (idsUnicos.length === 0) { this.isLoading = false; return; }

    idsUnicos.forEach(id => {
      this.clienteService.clienteById(id).subscribe({
        next: (cliente) => {
          this.clientesMap.set(id, cliente);
          if (++procesados === idsUnicos.length) { this.isLoading = false; }
        },
        error: () => {
          if (++procesados === idsUnicos.length) { this.isLoading = false; }
        }
      });
    });
  }

  /** Aplica todos los filtros activos sobre la lista completa de pedidos. */
  aplicarFiltros(): void {
    let resultado = [...this.todosPedidos];

    if (this.filtroEstado) {
      resultado = resultado.filter(p => p.estado === this.filtroEstado);
    }

    if (this.filtroFechaDesde) {
      const desde = new Date(this.filtroFechaDesde);
      resultado = resultado.filter(p => new Date(p.fechaCreacion) >= desde);
    }

    if (this.filtroFechaHasta) {
      // Incluir todo el día seleccionado
      const hasta = new Date(this.filtroFechaHasta);
      hasta.setHours(23, 59, 59, 999);
      resultado = resultado.filter(p => new Date(p.fechaCreacion) <= hasta);
    }

    this.pedidosFiltrados = resultado;
  }

  /** Al cambiar el filtro de producto, re-consulta al backend con ese filtro. */
  onProductoFiltroChange(): void {
    if (this.filtroProductoId) {
      this.cargarPedidos(this.filtroProductoId);
    } else {
      this.cargarPedidos();
    }
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroFechaDesde = '';
    this.filtroFechaHasta = '';
    this.filtroProductoId = null;
    this.cargarPedidos();
  }

  getNombreCliente(pedido: Pedido): string {
    const c = this.clientesMap.get(pedido.clienteId);
    return c ? `${c.nombre} ${c.apellido}` : `#${pedido.clienteId}`;
  }

  getEstadoColor(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':      return '#f2b705';
      case 'EN_PREPARACION': return '#3b82f6';
      case 'EN_CAMINO':      return '#8b5cf6';
      case 'ENTREGADO':      return '#16a34a';
      case 'CANCELADO':      return '#8c0e03';
      default:               return '#6b7280';
    }
  }

  formatearFecha(fecha: string | undefined): string {
    if (!fecha) { return '—'; }
    return new Date(fecha).toLocaleString('es-CO', {
      dateStyle: 'short',
      timeStyle: 'short',
    });
  }
}
