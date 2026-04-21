import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ProductoService } from '../../../services/producto.service';
import { AdicionalService } from '../../../services/adicional.service';
import {
  CrearPedidoRequest,
  SeleccionAdicionalRequest,
} from '../../../models/pedido';
import { Producto } from '../../../models/producto';
import { Adicional } from '../../../models/adicional';

@Component({
  selector: 'app-crear-pedido',
  templateUrl: './crear-pedido.component.html',
  styleUrls: ['./crear-pedido.component.css'],
})
export class CrearPedidoComponent implements OnInit {
  productos: Producto[] = [];
  todosLosAdicionales: Adicional[] = [];
  adicionalesFiltrados: Adicional[] = [];
  adicionalesSeleccionados: number[] = [];

  productoSeleccionadoId: number | null = null;
  productoSeleccionado: Producto | null = null;
  cantidadProductos: number = 1;

  // Para mostrar resumen
  precioProducto: number = 0;
  precioAdicionales: number = 0;
  precioTotal: number = 0;

  error: string = '';
  successMessage: string = '';
  isLoading: boolean = false;
  clienteEmail: string = '';

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly productoService: ProductoService,
    private readonly adicionalService: AdicionalService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.clienteEmail = localStorage.getItem('clienteEmail') || '';

    if (!this.clienteEmail) {
      this.router.navigate(['/login']);
      return;
    }

    this.cargarProductos();
    this.cargarAdicionales();

    // Recibir datos del sessionStorage (desde comida.component)
    const datosGuardados = sessionStorage.getItem('datosPedido');
    if (datosGuardados) {
      try {
        const state = JSON.parse(datosGuardados);
        this.procesarStateDelRouter(state);
        // Limpiar después de procesar
        sessionStorage.removeItem('datosPedido');
      } catch (e) {
        console.error('Error procesando datos del pedido', e);
      }
    }
  }

  private procesarStateDelRouter(state: any): void {
    if (state.productoId) {
      this.productoSeleccionadoId = state.productoId;
      this.precioProducto = state.productoPrecio || 0;

      // Esperar a que se carguen los productos para obtener la información completa
      const checkProducts = setInterval(() => {
        if (this.productos.length > 0) {
          this.productoSeleccionado =
            this.productos.find((p) => p.id === state.productoId) || null;

          if (this.productoSeleccionado) {
            this.precioProducto = this.productoSeleccionado.precio;

            // Filtrar adiciones por categoría del producto
            this.adicionalesFiltrados = this.todosLosAdicionales.filter(
              (adicional) =>
                adicional.categoria?.id ===
                this.productoSeleccionado?.categoria.id,
            );
          }

          clearInterval(checkProducts);
        }
      }, 100);

      // Si vienen adiciones preseleccionadas, cargarlas
      if (
        state.adicionesPreseleccionadas &&
        state.adicionesPreseleccionadas.length > 0
      ) {
        const checkAdiciones = setInterval(() => {
          if (this.adicionalesFiltrados.length > 0) {
            state.adicionesPreseleccionadas.forEach((adicional: any) => {
              if (
                this.adicionalesFiltrados.find((a) => a.id === adicional.id)
              ) {
                if (!this.adicionalesSeleccionados.includes(adicional.id)) {
                  this.adicionalesSeleccionados.push(adicional.id);
                }
              }
            });
            this.calcularTotal();
            clearInterval(checkAdiciones);
          }
        }, 100);
      }
    }
  }

  cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
      },
      error: () => {
        this.error = 'No fue posible cargar productos.';
      },
    });
  }

  cargarAdicionales(): void {
    this.adicionalService.getAdiciones().subscribe({
      next: (data) => {
        this.todosLosAdicionales = data;
      },
      error: () => {
        this.error = 'No fue posible cargar adicionales.';
      },
    });
  }

  onProductoChange(): void {
    if (!this.productoSeleccionadoId) {
      this.adicionalesFiltrados = [];
      this.productoSeleccionado = null;
      this.precioProducto = 0;
      return;
    }

    // Obtener el producto seleccionado
    this.productoSeleccionado =
      this.productos.find((p) => p.id === this.productoSeleccionadoId) || null;

    if (this.productoSeleccionado) {
      this.precioProducto = this.productoSeleccionado.precio;

      // Filtrar adiciones por categoría del producto
      this.adicionalesFiltrados = this.todosLosAdicionales.filter(
        (adicional) =>
          adicional.categoria?.id === this.productoSeleccionado?.categoria.id,
      );
    }

    this.adicionalesSeleccionados = [];
    this.calcularTotal();
  }

  onAdicionalChange(event: any, adicionalId: number): void {
    if (event.target.checked) {
      this.adicionalesSeleccionados.push(adicionalId);
    } else {
      this.adicionalesSeleccionados = this.adicionalesSeleccionados.filter(
        (id) => id !== adicionalId,
      );
    }
    this.calcularTotal();
  }

  private calcularTotal(): void {
    // Precio de adiciones
    this.precioAdicionales = this.adicionalesSeleccionados.reduce(
      (total, adicionId) => {
        const adicional = this.todosLosAdicionales.find(
          (a) => a.id === adicionId,
        );
        return total + (adicional?.precio || 0);
      },
      0,
    );

    // Total: (precio producto + adicionales) * cantidad
    this.precioTotal =
      (this.precioProducto + this.precioAdicionales) * this.cantidadProductos;
  }

  onCantidadChange(): void {
    this.calcularTotal();
  }

  onSubmit(): void {
    this.error = '';
    this.successMessage = '';

    if (!this.productoSeleccionadoId) {
      this.error = 'Debes seleccionar un producto.';
      return;
    }

    this.isLoading = true;

    const adiciones: SeleccionAdicionalRequest[] =
      this.adicionalesSeleccionados.map((id) => ({
        adicionalId: id,
        precio: 0,
      }));

    const request: CrearPedidoRequest = {
      items: [
        {
          productoId: this.productoSeleccionadoId,
          cantidad: this.cantidadProductos,
          adiciones,
        },
      ],
    };

    const clienteId = localStorage.getItem('clienteId') || '1';

    this.pedidoService.crearPedido(parseInt(clienteId), request).subscribe({
      next: () => {
        this.successMessage = 'Pedido creado exitosamente';
        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/pedidos/mis-pedidos']);
        }, 1500);
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'No fue posible crear el pedido.';
        this.isLoading = false;
      },
    });
  }

  onCancel(): void {
    this.router.navigate(['/']);
  }
}
