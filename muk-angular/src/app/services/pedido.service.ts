import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido, PedidoDetalle, CrearPedidoRequest } from '../models/pedido';

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
}
