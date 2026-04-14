import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Categoria } from '../../../../models/categoria';
import { AdicionalService } from '../../../../services/adicional.service';

interface AdicionFormModel {
  id?: number;
  nombre: string;
  categoriaId: number | null;
  precio: number | null;
}

@Component({
  selector: 'app-adicion-form',
  templateUrl: './adicion-form.component.html',
  styleUrls: ['./adicion-form.component.css']
})
export class AdicionFormComponent implements OnInit {
  categorias: Categoria[] = [];
  formData: AdicionFormModel = {
    nombre: '',
    categoriaId: null,
    precio: null
  };
  isEditMode = false;

  constructor(
    private readonly adicionalService: AdicionalService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.adicionalService.getCategorias().subscribe((categorias) => {
      this.categorias = categorias;
    });

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id) && id > 0) {
      this.adicionalService.getAdicionById(id).subscribe({
        next: (adicion) => {
          this.isEditMode = true;
          this.formData = {
            id: adicion.id,
            nombre: adicion.nombre,
            categoriaId: adicion.categoria?.id ?? null,
            precio: adicion.precio
          };
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

    const categoria = this.categorias.find((item) => item.id === Number(this.formData.categoriaId)) ?? null;

    this.adicionalService.saveAdicion({
      id: this.formData.id,
      nombre: this.formData.nombre,
      precio: this.formData.precio,
      categoria
    }).subscribe(() => {
      this.router.navigate(['/admin/adiciones']);
    });
  }
}
