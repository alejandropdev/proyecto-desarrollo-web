import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { OperarioAuthService } from '../../../services/operario-auth.service';
import { DomiciliarioService } from '../../../services/domiciliario.service';
import { Pedido } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';
import { Domiciliario } from '../../../models/adicional';

type FiltroEstado = 'TODOS' | 'PENDIENTE' | 'EN_PREPARACION' | 'LISTO' | 'EN_CAMINO' | 'COMPLETADO' | 'CANCELADO';

@Component({
  selector: 'app-pedido-portal',
  templateUrl: './pedido-portal.component.html',
  styleUrls: ['./pedido-portal.component.css']
})
export class PedidoPortalComponent implements OnInit {
  // Estado de la lista
  todosPedidos: Pedido[] = [];
  pedidosFiltrados: Pedido[] = [];
  
  // Filtros
  filtroActual: FiltroEstado = 'TODOS';
  filtrosDisponibles: FiltroEstado[] = ['TODOS', 'PENDIENTE', 'EN_PREPARACION', 'LISTO', 'EN_CAMINO', 'COMPLETADO', 'CANCELADO'];
  
  // UI
  isLoading: boolean = true;
  pedidoSeleccionado: Pedido | null = null;
  movedorDetalle: boolean = false;
  clientesMap: Map<number, Cliente> = new Map();
  domiciliariosDisponibles: Domiciliario[] = [];
  
  // Mensajes
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly operarioAuthService: OperarioAuthService,
    private readonly domiciliarioService: DomiciliarioService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.cargarPedidos();
    this.cargarDomiciliariosDisponibles();
  }

  cargarPedidos(): void {
    this.isLoading = true;
    this.pedidoService.listaPedidos().subscribe({
      next: (data) => {
        this.todosPedidos = data;
        this.aplicarFiltro();
        this.cargarNombresDeClientes(data);
      },
      error: (err) => {
        this.mensajeError = 'Error cargando pedidos';
        this.isLoading = false;
      }
    });
  }

  cargarDomiciliariosDisponibles(): void {
    this.domiciliarioService.obtenerActivosDisponibles().subscribe({
      next: (data: Domiciliario[]) => {
        this.domiciliariosDisponibles = data;
      },
      error: () => {
        console.log('Error cargando domiciliarios');
      }
    });
  }

  private cargarNombresDeClientes(pedidos: Pedido[]): void {
    const idsUnicos = [...new Set(pedidos.map(p => p.clienteId))];
    if (idsUnicos.length === 0) { this.isLoading = false; return; }

    let procesados = 0;
    idsUnicos.forEach((id: number) => {
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

  aplicarFiltro(): void {
    if (this.filtroActual === 'TODOS') {
      this.pedidosFiltrados = this.todosPedidos.filter(p => 
        !['COMPLETADO', 'CANCELADO'].includes(p.estado?.toUpperCase())
      );
    } else {
      this.pedidosFiltrados = this.todosPedidos.filter(p => 
        p.estado?.toUpperCase() === this.filtroActual
      );
    }
  }

  cambiarFiltro(nuevoFiltro: FiltroEstado): void {
    this.filtroActual = nuevoFiltro;
    this.aplicarFiltro();
    this.pedidoSeleccionado = null;
    this.movedorDetalle = false;
  }

  seleccionarPedido(pedido: Pedido): void {
    this.pedidoSeleccionado = pedido;
    this.movedorDetalle = true;
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  cerrarDetalle(): void {
    this.pedidoSeleccionado = null;
    this.movedorDetalle = false;
  }

  cambiarEstado(nuevoEstado: string): void {
    if (!this.pedidoSeleccionado) return;

    this.isLoading = true;
    this.pedidoService.cambiarEstado(this.pedidoSeleccionado.id, nuevoEstado).subscribe({
      next: (pedidoActualizado) => {
        // Actualizar la lista
        const index = this.todosPedidos.findIndex(p => p.id === pedidoActualizado.id);
        if (index > -1) {
          this.todosPedidos[index] = pedidoActualizado;
        }
        this.pedidoSeleccionado = pedidoActualizado;
        this.aplicarFiltro();
        this.mensajeExito = `Pedido actualizado a ${this.formatearEstado(nuevoEstado)}`;
        this.isLoading = false;
        this.cargarDomiciliariosDisponibles();
      },
      error: (err) => {
        this.mensajeError = err.error?.message || 'Error al cambiar estado del pedido';
        this.isLoading = false;
      }
    });
  }

  asignarDomiciliario(domiciliarioId: number): void {
    if (!this.pedidoSeleccionado) return;

    this.isLoading = true;
    this.pedidoService.asignarDomiciliario(this.pedidoSeleccionado.id, domiciliarioId).subscribe({
      next: (pedidoActualizado) => {
        const index = this.todosPedidos.findIndex(p => p.id === pedidoActualizado.id);
        if (index > -1) {
          this.todosPedidos[index] = pedidoActualizado;
        }
        this.pedidoSeleccionado = pedidoActualizado;
        this.aplicarFiltro();
        this.mensajeExito = 'Domiciliario asignado correctamente';
        this.isLoading = false;
        this.cargarDomiciliariosDisponibles();
      },
      error: (err) => {
        this.mensajeError = err.error?.message || 'Error al asignar domiciliario';
        this.isLoading = false;
      }
    });
  }

  recargar(): void {
    this.cargarPedidos();
    this.cargarDomiciliariosDisponibles();
    this.cerrarDetalle();
  }

  getNombreCliente(clienteId: number): string {
    const cliente = this.clientesMap.get(clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido}` : `Cliente #${clienteId}`;
  }

  getEstadoBadgeClass(estado: string): string {
    const est = estado?.toUpperCase() || '';
    switch (est) {
      case 'PENDIENTE': return 'badge-warning';
      case 'EN_PREPARACION': return 'badge-info';
      case 'LISTO': return 'badge-primary';
      case 'EN_CAMINO': return 'badge-purple';
      case 'COMPLETADO': return 'badge-success';
      case 'CANCELADO': return 'badge-danger';
      default: return 'badge-secondary';
    }
  }

  formatearEstado(estado: string): string {
    return estado?.toLowerCase().replace(/_/g, ' ').toUpperCase() || '';
  }

  contarPedidosPorFiltro(filtro: FiltroEstado): number {
    if (filtro === 'TODOS') {
      return this.todosPedidos.filter(p => !['COMPLETADO', 'CANCELADO'].includes(p.estado?.toUpperCase())).length;
    }
    return this.todosPedidos.filter(p => p.estado?.toUpperCase() === filtro).length;
  }

  cerrarSesion(): void {
    this.operarioAuthService.logout();
    this.router.navigate(['/operario/login']);
  }
}
