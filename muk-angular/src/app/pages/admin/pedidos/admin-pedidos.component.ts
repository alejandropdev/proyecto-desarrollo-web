import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { ProductoService } from '../../../services/producto.service';
import { DomiciliarioService } from '../../../services/domiciliario.service';
import { Pedido } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';
import { Producto } from '../../../models/producto';
import { Domiciliario } from '../../../models/domiciliario';

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
  domiciliarios: Domiciliario[] = [];

  isLoading: boolean = true;

  // Filtros
  filtroEstado: string = '';
  filtroFechaDesde: string = '';
  filtroFechaHasta: string = '';
  filtroProductoId: number | null = null;

  readonly estados = ['PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO'];

  // Dashboard - Estadísticas
  totalPedidos: number = 0;
  pedidosPendientes: number = 0;
  pedidosEnPreparacion: number = 0;
  pedidosEnCamino: number = 0;
  pedidosEntregados: number = 0;
  pedidosCancelados: number = 0;
  totalDomiciliarios: number = 0;
  domiciliariosDisponibles: number = 0;
  domiciliariosOcupados: number = 0;
  domiciliariosActivos: number = 0;
  porcentajeCompletados: number = 0;
  porcentajeOcupacion: number = 0;

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly productoService: ProductoService,
    private readonly domiciliarioService: DomiciliarioService,
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
    this.cargarDomiciliarios();
    this.cargarPedidos();
  }

  private cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => { this.productos = data; },
      error: () => { this.productos = []; }
    });
  }

  cargarDomiciliarios(): void {
    this.domiciliarioService.listarTodos().subscribe({
      next: (data) => {
        this.domiciliarios = data;
        this.calcularEstadisticasDomiciliarios();
      },
      error: () => {
        this.domiciliarios = [];
        this.calcularEstadisticasDomiciliarios();
      }
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
        this.calcularEstadisticas();
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

  private calcularEstadisticas(): void {
    this.totalPedidos = this.todosPedidos.length;
    this.pedidosPendientes = this.todosPedidos.filter(p => p.estado === 'PENDIENTE').length;
    this.pedidosEnPreparacion = this.todosPedidos.filter(p => p.estado === 'EN_PREPARACION').length;
    this.pedidosEnCamino = this.todosPedidos.filter(p => p.estado === 'EN_CAMINO').length;
    this.pedidosEntregados = this.todosPedidos.filter(p => p.estado === 'ENTREGADO').length;
    this.pedidosCancelados = this.todosPedidos.filter(p => p.estado === 'CANCELADO').length;
    
    this.porcentajeCompletados = this.totalPedidos > 0 
      ? Math.round((this.pedidosEntregados / this.totalPedidos) * 100) 
      : 0;
  }

  private calcularEstadisticasDomiciliarios(): void {
    this.totalDomiciliarios = this.domiciliarios.length;
    this.domiciliariosActivos = this.domiciliarios.filter(d => d.activo === true).length;
    this.domiciliariosDisponibles = this.domiciliarios.filter(d => d.disponible === true).length;
    this.domiciliariosOcupados = this.domiciliariosActivos - this.domiciliariosDisponibles;
    
    this.porcentajeOcupacion = this.domiciliariosActivos > 0
      ? Math.round((this.domiciliariosOcupados / this.domiciliariosActivos) * 100)
      : 0;
  }

  descargarCSV(): void {
    const timestamp = new Date().toLocaleString('es-CO', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    }).replace(/\//g, '-').replace(/,/g, '').replace(/:/g, '');

    const csvContent = [
      'Metrica;Valor',
      `Total de pedidos;${this.totalPedidos}`,
      `Pendientes;${this.pedidosPendientes}`,
      `En preparacion;${this.pedidosEnPreparacion}`,
      `En camino;${this.pedidosEnCamino}`,
      `Entregados;${this.pedidosEntregados}`,
      `Cancelados;${this.pedidosCancelados}`,
      `Porcentaje completados;${this.porcentajeCompletados}%`,
      `Total domiciliarios;${this.totalDomiciliarios}`,
      `Domiciliarios activos;${this.domiciliariosActivos}`,
      `Domiciliarios disponibles;${this.domiciliariosDisponibles}`,
      `Domiciliarios ocupados;${this.domiciliariosOcupados}`,
      `Porcentaje ocupacion;${this.porcentajeOcupacion}%`,
    ].join('\n');

    // Agregar BOM UTF-8 para compatibilidad con Excel
    const bom = '\uFEFF';
    const blob = new Blob([bom + csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', `muk-dashboard-${timestamp}.csv`);
    link.style.visibility = 'hidden';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
