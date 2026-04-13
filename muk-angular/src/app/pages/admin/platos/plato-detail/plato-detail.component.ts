import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Plato } from '../../../../models/plato';
import { PlatoService } from '../../../../services/plato.service';

@Component({
  selector: 'app-plato-detail',
  templateUrl: './plato-detail.component.html',
  styleUrls: ['./plato-detail.component.css']
})
export class PlatoDetailComponent implements OnInit {
  plato?: Plato;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly platoService: PlatoService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (Number.isNaN(id) || id <= 0) {
      this.router.navigate(['/admin/platos']);
      return;
    }
    this.platoService.getPlatoById(id).subscribe({
      next: (plato) => {
        this.plato = plato;
      },
      error: () => {
        this.router.navigate(['/admin/platos']);
      }
    });
  }

  onDelete(): void {
    if (!this.plato) {
      return;
    }
    if (!window.confirm('¿Eliminar este plato?')) {
      return;
    }
    this.platoService.deletePlato(this.plato.id).subscribe(() => {
      this.router.navigate(['/admin/platos']);
    });
  }
}
