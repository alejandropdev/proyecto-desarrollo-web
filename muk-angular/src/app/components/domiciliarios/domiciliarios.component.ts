import { Component, OnInit } from '@angular/core';
import { DomiciliarioService } from '../../services/domiciliario.service';
import { Domiciliario, DomiciliarioUpsertRequest } from '../../models/domiciliario';
import { PedidoService } from '../../services/pedido.service';
import { Pedido } from '../../models/pedido';

@Component({
  selector: 'app-domiciliarios',
  templateUrl: './domiciliarios.component.html',
  styleUrls: ['./domiciliarios.component.css'],
})
export class DomiciliariosComponent implements OnInit {
  domiciliarios: Domiciliario[] = [];
  pedidos: Pedido[] = [];

  domiciliarioSeleccionado: Domiciliario | null = null;
  pedidosDelDomiciliario: Pedido[] = [];

  cargando = false;
  mensajeError = '';
  mensajeExito = '';

  mostrarFormulario = false;
  editandoDomiciliario: Domiciliario | null = null;
  formularioEnvio = false;

  constructor(
    private domiciliarioService: DomiciliarioService,
    private pedidoService: PedidoService
  ) {}

  ngOnInit(): void {
    this.cargarDomiciliarios();
    this.cargarPedidos();
  }

  cargarDomiciliarios(): void {
    this.cargando = true;
    this.mensajeError = '';

    this.domiciliarioService.listarTodos().subscribe({
      next: (domiciliarios: Domiciliario[]) => {
        this.domiciliarios = domiciliarios;
        this.cargando = false;
      },
      error: (error) => {
        this.mensajeError = error?.error?.message || 'Error al cargar domiciliarios.';
        this.cargando = false;
      },
    });
  }

  cargarPedidos(): void {
    this.pedidoService.listaPedidos().subscribe({
      next: (pedidos: Pedido[]) => {
        this.pedidos = pedidos;

        if (this.domiciliarioSeleccionado) {
          this.verPedidos(this.domiciliarioSeleccionado);
        }
      },
      error: () => {
        console.error('Error al cargar pedidos.');
      },
    });
  }

  abrirFormularioNuevo(): void {
    this.editandoDomiciliario = null;
    this.mostrarFormulario = true;
  }

  abrirFormularioEditar(domiciliario: Domiciliario): void {
    this.editandoDomiciliario = { ...domiciliario };
    this.mostrarFormulario = true;
  }

  cerrarFormulario(): void {
    this.mostrarFormulario = false;
    this.editandoDomiciliario = null;
  }

  onGuardarDomiciliario(datos: DomiciliarioUpsertRequest): void {
    this.formularioEnvio = true;
    this.mensajeError = '';

    const operacion = this.editandoDomiciliario?.id
      ? this.domiciliarioService.actualizar(this.editandoDomiciliario.id, datos)
      : this.domiciliarioService.crear(datos);

    operacion.subscribe({
      next: () => {
        this.mensajeExito = this.editandoDomiciliario?.id
          ? 'Domiciliario actualizado correctamente.'
          : 'Domiciliario creado correctamente.';

        this.cerrarFormulario();
        this.cargarDomiciliarios();
        this.cargarPedidos();
        this.formularioEnvio = false;
        setTimeout(() => (this.mensajeExito = ''), 3000);
      },
      error: (error) => {
        this.mensajeError =
          error?.error?.message || 'Error al guardar domiciliario.';
        this.formularioEnvio = false;
      },
    });
  }

  onEliminar(id: number): void {
    if (!confirm('¿Estás seguro de que deseas eliminar este domiciliario?')) {
      return;
    }

    this.domiciliarioService.eliminar(id).subscribe({
      next: (response: any) => {
        this.mensajeExito =
          response?.message || 'Operación realizada correctamente.';

        this.cargarDomiciliarios();
        this.cargarPedidos();
        setTimeout(() => (this.mensajeExito = ''), 4000);
      },
      error: (error) => {
        this.mensajeError =
          error?.error?.message || 'Error al eliminar domiciliario.';
      },
    });
  }

  onToggleActivo(domiciliario: Domiciliario): void {
    const operacion = domiciliario.activo
      ? this.domiciliarioService.desactivar(domiciliario.id)
      : this.domiciliarioService.activar(domiciliario.id);

    operacion.subscribe({
      next: () => {
        const estado = domiciliario.activo ? 'desactivado' : 'activado';
        this.mensajeExito = `Domiciliario ${estado} correctamente.`;

        this.cargarDomiciliarios();
        this.cargarPedidos();
        setTimeout(() => (this.mensajeExito = ''), 3000);
      },
      error: (error) => {
        this.mensajeError =
          error?.error?.message || 'Error al cambiar estado.';
      },
    });
  }

  verPedidos(domiciliario: Domiciliario): void {
    this.domiciliarioSeleccionado = domiciliario;
    this.pedidosDelDomiciliario = this.pedidos.filter(
      (pedido) => pedido.domiciliarioId === domiciliario.id
    );
  }

  cerrarPedidos(): void {
    this.domiciliarioSeleccionado = null;
    this.pedidosDelDomiciliario = [];
  }

  pedidosPorEstado(estado: string): Pedido[] {
    return this.pedidosDelDomiciliario.filter(
      (pedido) => pedido.estado === estado
    );
  }

  cantidadEntregados(): number {
    return (
      this.pedidosPorEstado('COMPLETADO').length +
      this.pedidosPorEstado('ENTREGADO').length
    );
  }

  formatearEstado(estado: string): string {
    switch (estado) {
      case 'PENDIENTE':
        return 'Pendiente';
      case 'EN_PREPARACION':
        return 'En preparación';
      case 'LISTO':
        return 'Listo';
      case 'EN_CAMINO':
        return 'En camino';
      case 'COMPLETADO':
      case 'ENTREGADO':
        return 'Entregado';
      case 'CANCELADO':
        return 'Cancelado';
      default:
        return estado;
    }
  }

  getActivoColor(activo: boolean): string {
    return activo ? '#28a745' : '#dc3545';
  }

  getDisponibleColor(disponible: boolean): string {
    return disponible ? '#007bff' : '#ffc107';
  }
}