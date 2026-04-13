import { Component, OnInit } from '@angular/core';
import { Operador } from '../../../models/operador';
import { OperadorService } from '../../../services/operador.service';

@Component({
  selector: 'app-operadores',
  templateUrl: './operadores.component.html',
  styleUrls: ['./operadores.component.css']
})
export class OperadoresComponent implements OnInit {
  operadores: Operador[] = [];
  form = { id: 0, nombre: '', usuario: '', contrasena: '' };

  constructor(private readonly operadorService: OperadorService) {}

  ngOnInit(): void {
    this.load();
  }

  edit(operador: Operador): void {
    this.form = { id: operador.id, nombre: operador.nombre, usuario: operador.usuario, contrasena: '' };
  }

  save(): void {
    const payload = { nombre: this.form.nombre, usuario: this.form.usuario, contrasena: this.form.contrasena };
    const req = this.form.id
      ? this.operadorService.update(this.form.id, payload)
      : this.operadorService.create(payload);
    req.subscribe(() => {
      this.form = { id: 0, nombre: '', usuario: '', contrasena: '' };
      this.load();
    });
  }

  remove(id: number): void {
    if (!window.confirm('¿Eliminar operador?')) return;
    this.operadorService.delete(id).subscribe(() => this.load());
  }

  private load(): void {
    this.operadorService.list().subscribe((operadores) => (this.operadores = operadores));
  }
}
