import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdicionalService } from '../../../../services/adicional.service';

interface AdicionFormModel {
  id?: number;
  nombre: string;
  precio: number | null;
}

@Component({
  selector: 'app-adicion-form',
  templateUrl: './adicion-form.component.html',
  styleUrls: ['./adicion-form.component.css']
})
export class AdicionFormComponent implements OnInit {
  formData: AdicionFormModel = {
    nombre: '',
    precio: null
  };
  isEditMode = false;

  constructor(
    private readonly adicionalService: AdicionalService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isNaN(id) && id > 0) {
      const adicion = this.adicionalService.getAdicionById(id);
      if (!adicion) {
        this.router.navigate(['/admin/adiciones']);
        return;
      }
      this.isEditMode = true;
      this.formData = {
        id: adicion.id,
        nombre: adicion.nombre,
        precio: adicion.precio
      };
    }
  }

  onSubmit(form: NgForm): void {
    if (!form.valid || this.formData.precio === null) {
      return;
    }

    this.adicionalService.saveAdicion({
      id: this.formData.id,
      nombre: this.formData.nombre,
      precio: this.formData.precio
    });

    this.router.navigate(['/admin/adiciones']);
  }
}
