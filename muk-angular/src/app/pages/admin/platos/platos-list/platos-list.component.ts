import { Component, OnInit } from '@angular/core';
import { Plato } from '../../../../models/plato';
import { PlatoService } from '../../../../services/plato.service';

@Component({
  selector: 'app-platos-list',
  templateUrl: './platos-list.component.html',
  styleUrls: ['./platos-list.component.css']
})
export class PlatosListComponent implements OnInit {
  platos: Plato[] = [];

  constructor(private readonly platoService: PlatoService) {}

  ngOnInit(): void {
    this.loadPlatos();
  }

  onDelete(id: number): void {
    if (!window.confirm('¿Eliminar este plato?')) {
      return;
    }
    this.platoService.deletePlato(id);
    this.loadPlatos();
  }

  private loadPlatos(): void {
    this.platos = this.platoService.getPlatos();
  }
}
