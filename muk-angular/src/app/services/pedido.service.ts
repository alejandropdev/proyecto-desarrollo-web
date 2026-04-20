import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido } from '../models/pedido';

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private readonly apiUrl = '/api/pedidos';

  constructor(private readonly http: HttpClient) {}

  listaPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.apiUrl);
  }

  pedidoById(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${id}`);
  }

  pedidosPorOperador(operadorId: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/operador/${operadorId}`);
  }

  pedidosPorCliente(clienteId: number): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/cliente/${clienteId}`);
  }

  pedidosPorEstado(estado: string): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/estado/${estado}`);
  }
}
