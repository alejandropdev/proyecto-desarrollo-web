import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Categoria } from '../../../../models/categoria';
import { PlatoFormModel, PlatoService } from '../../../../services/plato.service';

@Component({
  selector: 'app-plato-form',
  templateUrl: './plato-form.component.html',
  styleUrls: ['./plato-form.component.css']
})
export class PlatoFormComponent implements OnInit {
  categorias: Categoria[] = [];
  formData: PlatoFormModel;
  isEditMode = false;

  constructor(
    private readonly platoService: PlatoService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.formData = this.platoService.buildInitialFormData();
  }

  ngOnInit(): void {
    this.platoService.getCategorias().subscribe((categorias) => {
      this.categorias = categorias;
    });
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id) && id > 0) {
      this.platoService.getPlatoById(id).subscribe({
        next: (plato) => {
          this.isEditMode = true;
          this.formData = this.platoService.mapToFormData(plato);
        },
        error: () => {
          this.router.navigate(['/admin/platos']);
        }
      });
    }
  }

  onSubmit(form: NgForm): void {
    if (!form.valid || this.formData.precio === null) {
      return;
    }

    this.platoService.savePlatoFromForm(this.formData).subscribe(() => {
      this.router.navigate(['/admin/platos']);
    });
  }
}
