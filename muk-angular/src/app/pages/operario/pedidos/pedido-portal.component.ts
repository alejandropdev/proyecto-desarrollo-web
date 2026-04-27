import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ClienteService } from '../../../services/cliente.service';
import { DomiciliarioService } from '../../../services/domiciliario.service';
import { OperarioAuthService } from '../../../services/operario-auth.service';
import { Pedido } from '../../../models/pedido';
import { Cliente } from '../../../models/cliente';
import { Domiciliario } from '../../../models/domiciliario';

@Component({
  selector: 'app-pedido-portal',
  templateUrl: './pedido-portal.component.html',
  styleUrls: ['./pedido-portal.component.css']
})
export class PedidoPortalComponent implements OnInit {
  /** Pedidos activos (excluye ENTREGADO y CANCELADO según requerimiento). */
  pedidos: Pedido[] = [];
  clientesMap: Map<number, Cliente> = new Map();
  isLoading: boolean = true;

  /** Modal de selección de domiciliario (al pasar a EN_CAMINO). */
  mostrarModal: boolean = false;
  domiciliariosDisponibles: Domiciliario[] = [];
  domiciliarioSeleccionadoId: number | null = null;
  pedidoEnTransicion: Pedido | null = null;

  errorAccion: string = '';
  procesando: boolean = false;

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly clienteService: ClienteService,
    private readonly domiciliarioService: DomiciliarioService,
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
        // Solo mostrar pedidos que no han sido completados ni cancelados
        this.pedidos = data.filter(p =>
          p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO'
        );
        this.cargarNombresDeClientes(this.pedidos);
      },
      error: () => { this.isLoading = false; }
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
          if (++procesados === idsUnicos.length) { this.isLoading = false; }
        },
        error: () => {
          if (++procesados === idsUnicos.length) { this.isLoading = false; }
        }
      });
    });
  }

  getNombreCliente(pedido: Pedido): string {
    const enMapa = this.clientesMap.get(pedido.clienteId);
    return enMapa ? `${enMapa.nombre} ${enMapa.apellido}` : `CLIENTE #${pedido.clienteId}`;
  }

  getEstadoColor(estado: string): string {
    switch (estado?.toUpperCase()) {
      case 'PENDIENTE':      return '#f2b705';
      case 'EN_PREPARACION': return '#3b82f6';
      case 'EN_CAMINO':      return '#8b5cf6';
      case 'ENTREGADO':      return '#34100b';
      case 'CANCELADO':      return '#8c0e03';
      default:               return '#6b7280';
    }
  }

  /** Devuelve la label del botón de avance según el estado actual. */
  getLabelBotonAvance(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':      return 'Cocinar';
      case 'EN_PREPARACION': return 'Enviar';
      case 'EN_CAMINO':      return 'Entregar';
      default:               return '';
    }
  }

  /** Devuelve el próximo estado en la cadena. */
  getSiguienteEstado(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':      return 'EN_PREPARACION';
      case 'EN_PREPARACION': return 'EN_CAMINO';
      case 'EN_CAMINO':      return 'ENTREGADO';
      default:               return '';
    }
  }

  /** Maneja el avance de estado; si el siguiente es EN_CAMINO, abre el modal. */
  avanzarEstado(pedido: Pedido): void {
    const siguiente = this.getSiguienteEstado(pedido.estado);
    if (!siguiente) { return; }
    this.errorAccion = '';

    if (siguiente === 'EN_CAMINO') {
      this.abrirModalDomiciliario(pedido);
    } else {
      this.cambiarEstado(pedido.id, siguiente, undefined);
    }
  }

  private abrirModalDomiciliario(pedido: Pedido): void {
    this.pedidoEnTransicion = pedido;
    this.domiciliarioSeleccionadoId = null;
    this.mostrarModal = true;

    this.domiciliarioService.listarDisponibles().subscribe({
      next: (lista) => { this.domiciliariosDisponibles = lista; },
      error: () => { this.errorAccion = 'No se pudo cargar la lista de domiciliarios.'; }
    });
  }

  confirmarEnvio(): void {
    if (!this.pedidoEnTransicion || !this.domiciliarioSeleccionadoId) {
      this.errorAccion = 'Debes seleccionar un domiciliario.';
      return;
    }
    this.cambiarEstado(
      this.pedidoEnTransicion.id,
      'EN_CAMINO',
      this.domiciliarioSeleccionadoId
    );
    this.cerrarModal();
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.pedidoEnTransicion = null;
    this.domiciliarioSeleccionadoId = null;
    this.domiciliariosDisponibles = [];
  }

  private cambiarEstado(pedidoId: number, nuevoEstado: string, domiciliarioId?: number): void {
    this.procesando = true;
    this.pedidoService.actualizarEstado(pedidoId, nuevoEstado, domiciliarioId).subscribe({
      next: () => {
        this.procesando = false;
        this.cargarPedidos();
      },
      error: (err) => {
        this.procesando = false;
        this.errorAccion = err?.error?.message ?? 'No se pudo actualizar el estado.';
      }
    });
  }

  verDetalle(id: number): void {
    this.router.navigate(['/pedidos/detalle', id]);
  }

  cerrarSesion(): void {
    this.operarioAuthService.logout();
    this.router.navigate(['/operario/login']);
  }
}
