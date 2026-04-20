import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Categoria } from '../../../../models/categoria';
import { AdicionFormModel, AdicionalService } from '../../../../services/adicional.service';

@Component({
  selector: 'app-adicion-form',
  templateUrl: './adicion-form.component.html',
  styleUrls: ['./adicion-form.component.css']
})
export class AdicionFormComponent implements OnInit {
  categorias: Categoria[] = [];
  formData: AdicionFormModel;
  isEditMode = false;

  constructor(
    private readonly adicionalService: AdicionalService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.formData = this.adicionalService.buildInitialFormData();
  }

  ngOnInit(): void {
    this.adicionalService.getCategorias().subscribe((categorias) => {
      this.categorias = categorias;
    });

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id) && id > 0) {
      this.adicionalService.getAdicionById(id).subscribe({
        next: (adicion) => {
          this.isEditMode = true;
          this.formData = this.adicionalService.mapToFormData(adicion);
        },
        error: () => {
          this.router.navigate(['/admin/adiciones']);
        }
      });
    }
  }

  onSubmit(form: NgForm): void {
    if (!form.valid || this.formData.precio === null) {
      return;
    }

    this.adicionalService.saveAdicionFromForm(this.formData).subscribe(() => {
      this.router.navigate(['/admin/adiciones']);
    });
  }
}
