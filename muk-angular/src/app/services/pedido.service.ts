import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Pedido,
  PedidoDetalle,
  CrearPedidoRequest,
} from '../models/pedido';

@Injectable({
  providedIn: 'root',
})
export class PedidoService {
  private readonly apiUrl = '/api/pedidos';

  constructor(private readonly http: HttpClient) {}

  crearPedido(clienteId: number, request: CrearPedidoRequest): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.apiUrl}?clienteId=${clienteId}`, request);
  }

  listaPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.apiUrl);
  }

  obtenerPedidosNoCompletados(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/sin-completar/lista`);
  }

  obtenerPedidosPorCliente(clienteId: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}?clienteId=${clienteId}`);
  }

  obtenerPedidoDetalle(id: number): Observable<PedidoDetalle> {
    return this.http.get<PedidoDetalle>(`${this.apiUrl}/${id}`);
  }

  cambiarEstado(pedidoId: number, nuevoEstado: string): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.apiUrl}/${pedidoId}/cambiar-estado`, {
      nuevoEstado,
    });
  }

  actualizarEstado(pedidoId: number, nuevoEstado: string): Observable<Pedido> {
    return this.cambiarEstado(pedidoId, nuevoEstado);
  }

  listarTodos(productoId?: number): Observable<Pedido[]> {
    const params = productoId ? `?productoId=${productoId}` : '';
    return this.http.get<Pedido[]>(`${this.apiUrl}${params}`);
  }
}