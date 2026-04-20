import { Component, OnInit } from '@angular/core';
import { Operador } from '../../../models/operador';
import { OperadorFormState, OperadorService } from '../../../services/operador.service';

@Component({
  selector: 'app-operadores',
  templateUrl: './operadores.component.html',
  styleUrls: ['./operadores.component.css']
})
export class OperadoresComponent implements OnInit {
  operadores: Operador[] = [];
  form: OperadorFormState;

  constructor(private readonly operadorService: OperadorService) {
    this.form = this.operadorService.buildInitialFormState();
  }

  ngOnInit(): void {
    this.load();
  }

  edit(operador: Operador): void {
    this.form = this.operadorService.mapToFormState(operador);
  }

  save(): void {
    this.operadorService.save(this.form).subscribe(() => {
      this.form = this.operadorService.buildInitialFormState();
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
