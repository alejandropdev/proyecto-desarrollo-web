import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Adicional } from '../../../../models/adicional';
import { AdicionalService } from '../../../../services/adicional.service';

@Component({
  selector: 'app-adicion-detail',
  templateUrl: './adicion-detail.component.html',
  styleUrls: ['./adicion-detail.component.css']
})
export class AdicionDetailComponent implements OnInit {
  adicion?: Adicional;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly adicionalService: AdicionalService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (Number.isNaN(id) || id <= 0) {
      this.router.navigate(['/admin/adiciones']);
      return;
    }
    this.adicion = this.adicionalService.getAdicionById(id);
    if (!this.adicion) {
      this.router.navigate(['/admin/adiciones']);
    }
  }

  onDelete(): void {
    if (!this.adicion) {
      return;
    }
    if (!window.confirm('¿Eliminar esta adicion?')) {
      return;
    }
    this.adicionalService.deleteAdicion(this.adicion.id);
    this.router.navigate(['/admin/adiciones']);
  }
}
