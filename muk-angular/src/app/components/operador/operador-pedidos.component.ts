import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../../services/pedido.service';
import { DomiciliarioService } from '../../services/domiciliario.service';
import { Pedido, EstadoPedido } from '../../models/pedido';
import { Domiciliario } from '../../models/domiciliario';


type FiltroEstado = 'TODOS' | 'PENDIENTE' | 'EN_PREPARACION' | 'EN_CAMINO' | 'ENTREGADO' | 'CANCELADO';

/**
 * Componente para el portal del operador.
 * 
 * Responsabilidades:
 * - Mostrar lista de pedidos con filtros
 * - Permitir cambiar estado de los pedidos
 * - Permitir asignar domiciliarios
 * - Por defecto mostrar solo NO completados
 */
@Component({
  selector: 'app-operador-pedidos',
  templateUrl: './operador-pedidos.component.html',
  styleUrls: ['./operador-pedidos.component.css'],
})
export class OperadorPedidosComponent implements OnInit {
  // Todos los pedidos cargados
  todosPedidos: Pedido[] = [];
  
  // Pedidos filtrados para mostrar
  pedidosFiltrados: Pedido[] = [];

  // Domiciliarios disponibles
  domiciliariosDisponibles: Domiciliario[] = [];

  // Filtro actual
  filtroActual: FiltroEstado = 'TODOS';
  filtrosDisponibles: FiltroEstado[] = ['TODOS', 'PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'CANCELADO'];

  // Control de carga
  cargando: boolean = false;
  mensajeError: string = '';
  mensajeExito: string = '';

  // Estados disponibles para cambiar
  estadosDisponibles = Object.values(EstadoPedido);

  constructor(
    private pedidoService: PedidoService,
    private domiciliarioService: DomiciliarioService
  ) {}

  ngOnInit(): void {
    this.cargarPedidos();
    this.cargarDomiciliariosDisponibles();
  }

  /**
   * Carga todos los pedidos del sistema
   */
  cargarPedidos(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.pedidoService.obtenerPedidosNoCompletados().subscribe({
      next: (pedidos: Pedido[]) => {
        this.todosPedidos = pedidos;
        this.aplicarFiltro();
        this.cargando = false;
        console.log('Pedidos cargados:', pedidos.length);
      },
      error: (error) => {
        this.mensajeError = 'Error al cargar los pedidos. Intenta nuevamente.';
        this.cargando = false;
        console.error('Error:', error);
      },
    });
  }

  /**
   * Carga los domiciliarios activos y disponibles
   */
  cargarDomiciliariosDisponibles(): void {
    this.domiciliarioService.obtenerActivosDisponibles().subscribe({
      next: (domiciliarios: Domiciliario[]) => {
        this.domiciliariosDisponibles = domiciliarios;
        console.log('Domiciliarios disponibles:', domiciliarios.length);
      },
      error: (error) => {
        console.error('Error al cargar domiciliarios:', error);
      },
    });
  }

  /**
   * Aplica el filtro actual a los pedidos
   */
  aplicarFiltro(): void {
    if (this.filtroActual === 'TODOS') {
      this.pedidosFiltrados = this.todosPedidos;
    } else {
      this.pedidosFiltrados = this.todosPedidos.filter(
        (pedido) => pedido.estado === this.filtroActual
      );
    }
  }

  /**
   * Cambia el filtro activo
   */
  cambiarFiltro(nuevoFiltro: FiltroEstado): void {
    this.filtroActual = nuevoFiltro;
    this.aplicarFiltro();
  }

  /**
   * Manejador cuando el usuario cambia el estado de un pedido
   */
  onCambiarEstado(evento: { pedidoId: number; nuevoEstado: string }): void {
    this.pedidoService.cambiarEstado(evento.pedidoId, evento.nuevoEstado).subscribe({
      next: () => {
        this.mensajeExito = `Pedido actualizado a ${evento.nuevoEstado}`;
        this.cargarPedidos();
        this.cargarDomiciliariosDisponibles();
        setTimeout(() => (this.mensajeExito = ''), 3000);
      },
      error: (error) => {
        this.mensajeError = `Error: ${error?.error?.message || 'Error al cambiar estado'}`;
        console.error('Error:', error);
      },
    });
  }

  /**
   * Recarga manualmente la lista
   */
  recargar(): void {
    this.cargarPedidos();
    this.cargarDomiciliariosDisponibles();
  }

  /**
   * Obtiene el badge de color para un estado
   */
  getEstadoBadgeClass(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':
        return 'badge-warning';
      case 'EN_PREPARACION':
        return 'badge-info';
      case 'EN_CAMINO':
        return 'badge-primary';
      case 'ENTREGADO':
        return 'badge-success';
      case 'COMPLETADO':
        return 'badge-success';
      case 'CANCELADO':
        return 'badge-danger';
      default:
        return 'badge-secondary';
    }
  }
}
