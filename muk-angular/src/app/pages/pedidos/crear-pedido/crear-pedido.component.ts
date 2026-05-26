import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PedidoService } from '../../../services/pedido.service';
import { ProductoService } from '../../../services/producto.service';
import { CarritoService } from '../../../services/carrito.service';
import {
  CrearPedidoRequest,
  ItemPedidoRequest,
  SeleccionAdicionalRequest,
} from '../../../models/pedido';
import { Producto } from '../../../models/producto';
import { Adicional } from '../../../models/adicional';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface LineaPedido {
  uid: number;
  productoId: number | null;
  producto: Producto | null;
  cantidad: number;
  adicionalesFiltrados: Adicional[];
  adicionalesSeleccionados: number[];
}

@Component({
  selector: 'app-crear-pedido',
  templateUrl: './crear-pedido.component.html',
  styleUrls: ['./crear-pedido.component.css'],
})
export class CrearPedidoComponent implements OnInit {
  productos: Producto[] = [];

  lineas: LineaPedido[] = [];

  error: string = '';
  successMessage: string = '';
  isLoading: boolean = false;
  clienteEmail: string = '';

  private uidCounter: number = 0;
  private productosCargados: boolean = false;
  private cacheAdicionesPermitidas: Map<number, Adicional[]> = new Map();

  constructor(
    private readonly pedidoService: PedidoService,
    private readonly productoService: ProductoService,
    private readonly carritoService: CarritoService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.clienteEmail = localStorage.getItem('clienteEmail') || '';

    if (!this.clienteEmail) {
      this.router.navigate(['/login']);
      return;
    }

    this.cargarProductos();
  }

  // === Carga inicial de datos ===

  cargarProductos(): void {
    this.productoService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.productosCargados = true;
        this.intentarInicializarLineas();
      },
      error: () => {
        this.error = 'No fue posible cargar productos.';
      },
    });
  }

  private intentarInicializarLineas(): void {
    if (!this.productosCargados) {
      return;
    }
    if (this.lineas.length > 0) {
      return;
    }

    // Prioridad 1: carrito guardado en localStorage
    const guardadas = this.carritoService.cargar();
    if (guardadas.length > 0) {
      this.lineas = guardadas.map((g) => {
        const linea = this.crearLineaVacia();
        linea.productoId = g.productoId;
        this.aplicarCambioProductoAsinc(linea).subscribe(() => {
          linea.cantidad = g.cantidad;
          linea.adicionalesSeleccionados = g.adicionalesSeleccionados.filter(
            (id) => linea.adicionalesFiltrados.some((ad) => ad.id === id),
          );
        });
        return linea;
      });
      return;
    }

    // Sin datos previos: línea vacía inicial
    this.lineas = [this.crearLineaVacia()];
  }

  /** Persiste el estado actual del carrito en localStorage. */
  private persistirCarrito(): void {
    this.carritoService.guardar(
      this.lineas.map((l) => ({
        productoId: l.productoId as number,
        cantidad: l.cantidad,
        adicionalesSeleccionados: l.adicionalesSeleccionados,
      })),
    );
  }

  // === Gestión de líneas (carrito) ===

  private crearLineaVacia(): LineaPedido {
    return {
      uid: ++this.uidCounter,
      productoId: null,
      producto: null,
      cantidad: 1,
      adicionalesFiltrados: [],
      adicionalesSeleccionados: [],
    };
  }

  agregarLinea(): void {
    this.lineas = [...this.lineas, this.crearLineaVacia()];
    this.persistirCarrito();
  }

  eliminarLinea(uid: number): void {
    this.lineas = this.lineas.filter((l) => l.uid !== uid);
    if (this.lineas.length === 0) {
      this.lineas = [this.crearLineaVacia()];
    }
    this.persistirCarrito();
  }

  onProductoChangeLinea(linea: LineaPedido): void {
    this.aplicarCambioProductoAsinc(linea).subscribe(() => {
      this.persistirCarrito();
    });
  }

  private aplicarCambioProductoAsinc(linea: LineaPedido): Observable<void> {
    if (!linea.productoId) {
      linea.producto = null;
      linea.adicionalesFiltrados = [];
      linea.adicionalesSeleccionados = [];
      return new Observable((observer) => {
        observer.next();
        observer.complete();
      });
    }

    linea.producto =
      this.productos.find((p) => p.id === linea.productoId) ?? null;

    if (!linea.producto) {
      linea.adicionalesFiltrados = [];
      linea.adicionalesSeleccionados = [];
      return new Observable((observer) => {
        observer.next();
        observer.complete();
      });
    }

    const productoId = linea.productoId as number;

    // Verificar si ya está en cache
    if (this.cacheAdicionesPermitidas.has(productoId)) {
      linea.adicionalesFiltrados =
        this.cacheAdicionesPermitidas.get(productoId) ?? [];
      linea.adicionalesSeleccionados = [];
      return new Observable((observer) => {
        observer.next();
        observer.complete();
      });
    }

    // Obtener adiciones permitidas del servidor
    return this.productoService.obtenerAdicionalesPermitidos(productoId).pipe(
      map((adiciones: Adicional[]) => {
        this.cacheAdicionesPermitidas.set(productoId, adiciones);
        linea.adicionalesFiltrados = adiciones;
        linea.adicionalesSeleccionados = [];
      }),
    );
  }

  private aplicarCambioProducto(linea: LineaPedido): void {
    if (!linea.productoId) {
      linea.producto = null;
      linea.adicionalesFiltrados = [];
      linea.adicionalesSeleccionados = [];
      return;
    }
    linea.producto =
      this.productos.find((p) => p.id === linea.productoId) ?? null;
    if (linea.producto) {
      const productoId = linea.productoId as number;
      // Verificar si está en cache
      if (this.cacheAdicionesPermitidas.has(productoId)) {
        linea.adicionalesFiltrados =
          this.cacheAdicionesPermitidas.get(productoId) ?? [];
      } else {
        linea.adicionalesFiltrados = [];
      }
    } else {
      linea.adicionalesFiltrados = [];
    }
    linea.adicionalesSeleccionados = [];
  }

  onAdicionalChangeLinea(
    linea: LineaPedido,
    event: any,
    adicionalId: number,
  ): void {
    if (event.target.checked) {
      if (!linea.adicionalesSeleccionados.includes(adicionalId)) {
        linea.adicionalesSeleccionados.push(adicionalId);
      }
    } else {
      linea.adicionalesSeleccionados = linea.adicionalesSeleccionados.filter(
        (id) => id !== adicionalId,
      );
    }
    this.persistirCarrito();
  }

  onCantidadChangeLinea(): void {
    this.persistirCarrito();
  }

  // === Cálculos de precio ===

  precioAdicionalesLinea(linea: LineaPedido): number {
    return linea.adicionalesSeleccionados.reduce((total, adicionId) => {
      const adicional = linea.adicionalesFiltrados.find(
        (a) => a.id === adicionId,
      );
      return total + (adicional?.precio ?? 0);
    }, 0);
  }

  subtotalLinea(linea: LineaPedido): number {
    const precioProducto = linea.producto?.precio ?? 0;
    return (
      (precioProducto + this.precioAdicionalesLinea(linea)) *
      (linea.cantidad || 0)
    );
  }

  totalProductos(): number {
    return this.lineas.reduce((total, l) => {
      const precio = l.producto?.precio ?? 0;
      return total + precio * (l.cantidad || 0);
    }, 0);
  }

  totalAdiciones(): number {
    return this.lineas.reduce((total, l) => {
      return total + this.precioAdicionalesLinea(l) * (l.cantidad || 0);
    }, 0);
  }

  totalGeneral(): number {
    return this.totalProductos() + this.totalAdiciones();
  }

  cantidadTotalItems(): number {
    return this.lineas.reduce((t, l) => t + (l.cantidad || 0), 0);
  }

  // === Envío ===

  onSubmit(): void {
    this.error = '';
    this.successMessage = '';

    const lineasValidas = this.lineas.filter(
      (l) => l.productoId && l.cantidad && l.cantidad > 0,
    );

    if (lineasValidas.length === 0) {
      this.error = 'Debes agregar al menos un producto al pedido.';
      return;
    }

    this.isLoading = true;

    const items: ItemPedidoRequest[] = lineasValidas.map((l) => {
      const adiciones: SeleccionAdicionalRequest[] =
        l.adicionalesSeleccionados.map((id) => {
          const adicional = l.adicionalesFiltrados.find((a) => a.id === id);
          return {
            adicionalId: id,
            precio: adicional?.precio ?? 0,
          };
        });
      return {
        productoId: l.productoId as number,
        cantidad: l.cantidad,
        adiciones,
      };
    });

    const request: CrearPedidoRequest = { items };

    const clienteId = localStorage.getItem('clienteId') || '1';

    this.pedidoService.crearPedido(parseInt(clienteId), request).subscribe({
      next: () => {
        this.carritoService.limpiar();
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
    this.router.navigate(['/menu']);
  }

  // === Helpers de template ===

  trackLineaByUid(_index: number, linea: LineaPedido): number {
    return linea.uid;
  }

  estaSeleccionado(linea: LineaPedido, adicionalId: number): boolean {
    return linea.adicionalesSeleccionados.includes(adicionalId);
  }

  puedeEnviar(): boolean {
    return (
      !this.isLoading &&
      this.lineas.some((l) => l.productoId && l.cantidad && l.cantidad > 0)
    );
  }
}
