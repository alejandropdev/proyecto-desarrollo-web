import { Component, OnInit } from '@angular/core';
import { Adicional } from '../../../../models/adicional';
import { AdicionalService } from '../../../../services/adicional.service';

@Component({
  selector: 'app-adiciones-list',
  templateUrl: './adiciones-list.component.html',
  styleUrls: ['./adiciones-list.component.css']
})
export class AdicionesListComponent implements OnInit {
  adiciones: Adicional[] = [];

  constructor(private readonly adicionalService: AdicionalService) {}

  ngOnInit(): void {
    this.loadAdiciones();
  }

  onDelete(id: number): void {
    if (!window.confirm('¿Eliminar esta adicion?')) {
      return;
    }
    this.adicionalService.deleteAdicion(id);
    this.loadAdiciones();
  }

  private loadAdiciones(): void {
    this.adiciones = this.adicionalService.getAdiciones();
  }
}
