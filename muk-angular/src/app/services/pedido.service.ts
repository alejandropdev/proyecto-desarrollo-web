import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido, PedidoDetalle, CrearPedidoRequest, CambiarEstadoPedidoRequest } from '../models/pedido';

@Injectable({
  providedIn: 'root',
})
export class PedidoService {
  private readonly apiUrl = '/api/pedidos';

  constructor(private readonly http: HttpClient) {}

  /**
   * Crea un nuevo pedido
   */
  crearPedido(
    clienteId: number,
    request: CrearPedidoRequest,
  ): Observable<Pedido> {
    return this.http.post<Pedido>(
      `${this.apiUrl}?clienteId=${clienteId}`,
      request,
    );
  }

  /**
   * Obtiene todos los pedidos (para operarios)
   */
  listaPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.apiUrl);
  }

  /**
   * Obtiene los pedidos de un cliente
   */
  obtenerPedidosPorCliente(clienteId: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}?clienteId=${clienteId}`);
  }

  /**
   * Obtiene el detalle completo de un pedido
   */
  obtenerPedidoDetalle(id: number): Observable<PedidoDetalle> {
    return this.http.get<PedidoDetalle>(`${this.apiUrl}/${id}`);
  }

  /**
   * Obtiene todos los pedidos NO completados.
   * Estados NO completados: PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO
   * Usado por el portal de operadores
   */
  obtenerPedidosNoCompletados(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/sin-completar/lista`);
  }

  /**
   * Cambia el estado de un pedido.
   * 
   * Estados válidos: PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO, COMPLETADO, CANCELADO
   * 
   * Lógica especial:
   * - Si estado = EN_CAMINO → domiciliario.disponible = false (si está asignado)
   * - Si estado = COMPLETADO → domiciliario.disponible = true (si está asignado)
   */
  cambiarEstado(pedidoId: number, nuevoEstado: string): Observable<Pedido> {
    const request: CambiarEstadoPedidoRequest = { nuevoEstado };
    return this.http.put<Pedido>(`${this.apiUrl}/${pedidoId}/cambiar-estado`, request);
  }

  /**
   * Asigna un domiciliario a un pedido.
   * El domiciliario debe estar activo y disponible.
   */
  asignarDomiciliario(pedidoId: number, domiciliarioId: number): Observable<Pedido> {
    const request = { domiciliarioId };
    return this.http.put<Pedido>(`${this.apiUrl}/${pedidoId}/asignar-domiciliario`, request);
  }
}
