import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Categoria } from '../../../../models/categoria';
import { PlatoService } from '../../../../services/plato.service';

interface PlatoFormModel {
  id?: number;
  nombre: string;
  categoriaId: number | null;
  precio: number | null;
  imagenUrl: string;
  descripcion: string;
}

@Component({
  selector: 'app-plato-form',
  templateUrl: './plato-form.component.html',
  styleUrls: ['./plato-form.component.css']
})
export class PlatoFormComponent implements OnInit {
  categorias: Categoria[] = [];
  formData: PlatoFormModel = {
    nombre: '',
    categoriaId: null,
    precio: null,
    imagenUrl: '',
    descripcion: ''
  };
  isEditMode = false;

  constructor(
    private readonly platoService: PlatoService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.platoService.getCategorias().subscribe((categorias) => {
      this.categorias = categorias;
    });
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id) && id > 0) {
      this.platoService.getPlatoById(id).subscribe({
        next: (plato) => {
          this.isEditMode = true;
          this.formData = {
            id: plato.id,
            nombre: plato.nombre,
            categoriaId: plato.categoria?.id ?? null,
            precio: plato.precio,
            imagenUrl: plato.imagenUrl,
            descripcion: plato.descripcion
          };
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

    const categoria = this.categorias.find((item) => item.id === Number(this.formData.categoriaId)) ?? null;

    this.platoService.savePlato({
      id: this.formData.id,
      nombre: this.formData.nombre,
      descripcion: this.formData.descripcion,
      precio: this.formData.precio,
      imagenUrl: this.formData.imagenUrl,
      categoria
    }).subscribe(() => {
      this.router.navigate(['/admin/platos']);
    });
  }
}
